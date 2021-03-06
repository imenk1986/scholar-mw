package net.vpc.scholar.hadrumaths.cache;

import net.vpc.scholar.hadrumaths.Chronometer;
import net.vpc.scholar.hadrumaths.Maths;
import net.vpc.scholar.hadrumaths.util.*;
import net.vpc.scholar.hadrumaths.util.dump.Dumper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 * @creationtime 2 juin 2007 13:28:08
 */
public class PersistenceCache implements PersistentCacheConfig {
    private static Logger log = Logger.getLogger(PersistenceCache.class.getName());
    public static final int LOCK_TIMEOUT = 1000 * 3600 * 24 * 7;

//    public static void main(String[] args) {
//        final PersistenceCache c = new PersistenceCache();
//        for (int i = 0; i < 3; i++) {
//            new Thread() {
//                @Override
//                public void run() {
//                    c.evaluate("toto", new Evaluator() {
//                        @Override
//                        public Object evaluate(Object[] args) {
//                            System.out.println("START ....");
//                            try {
//                                Thread.sleep(20000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            System.out.println("END ....");
//                            return "Hello";
//                        }
//                    });
//                }
//            }.start();
//        }
//    }

    private PersistenceCache parent;
    private PathFS pathFS = new PathFS();
    private HFile rootFolder;
    private HFile rootFolderCached;
    private HFile repositoryFolder;
    private String repositoryName;
    private String dumpFileName;
    private CacheMode mode = CacheMode.INHERITED;
    private HFileFilter cacheFileFilter;
    private boolean logLoadStatsEnabled = false;
    /**
     * tasks that take more than taskTimeThreshold (in ms)
     * will be logged to console
     */
    private long minimumTimeForCache = 0;
    private long taskTimeThreshold = 3000;
    private boolean ignorePrevious = false;

//    public PersistenceCache() {
//        this((File) null);
//    }
//
//    public PersistenceCache(File cache) {
//        this("dump", cache, null);
//    }
//
//    public PersistenceCache(String id, String cache) {
//        this(id, validateFolder(cache), null);
//    }
//
//    public PersistenceCache(String id) {
//        this(id, (File) null, null);
//    }

    public PersistenceCache() {
        this(null, null, null);
    }

    public PersistenceCache(String repositoryName) {
        this(null, repositoryName, null);
    }

    public PersistenceCache(HFile rootFolder, String repositoryName, PersistenceCache parent) {
        this.parent = parent;
        this.rootFolder = rootFolder;
        this.repositoryName = repositoryName;
        this.dumpFileName = "dump" + ObjectCache.CACHE_DEF_SUFFIX;
        cacheFileFilter = new HFileFilter() {

            public boolean accept(HFile pathname) {
                return pathname.isDirectory()
                        ||
                        (pathname.isFile() &&
                                (pathname.getName().equals(PersistenceCache.this.dumpFileName)
                                        || pathname.getName().endsWith(ObjectCache.CACHE_LOG_SUFFIX)
                                        || pathname.getName().endsWith(ObjectCache.CACHE_OBJECT_SUFFIX)));
            }
        };
    }


    public HFile getRepositoryFolder() {
        if (repositoryFolder == null) {
            String n = getRepositoryName();
            if (n == null || n.length() == 0) {
                n = "default";
            }
            repositoryFolder = new HFile(getRootFolder(), n);
        }
        return repositoryFolder;
    }

    public HFile getRootFolder() {
        if (rootFolderCached == null) {
            if (rootFolder == null) {
                rootFolderCached = Maths.Config.getCacheFileSystem().get("/");
            } else {
                rootFolderCached = rootFolder;
            }
        }
        return rootFolderCached;
    }


    //    private MomCache loadDump() throws IOException {
//        return loadDump(getCacheFolder());
//    }
    private ObjectCache loadCache(HFile folder) throws IOException {
        HFile dumpFile = new HFile(folder, dumpFileName);
        if (!dumpFile.existsOrWait() || !dumpFile.isFile()) {
            return null;
        }
        InputStream fileInputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            fileInputStream = dumpFile.getInputStream();
            int x;
            byte[] b = new byte[1024 * 4];
            while (true) {
                x = fileInputStream.read(b);
                if (x <= 0) {
                    break;
                }
                sb.append(new String(b, 0, x));
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return new ObjectCache(new DumpPath(sb.toString()), this);
    }

    public HFile getFolder(String dump) {
        return getDumpFolder(dump, false);
    }

    public String getDumpHashCode(String dump) {
        String hh = Integer.toString(dump.hashCode(), 36).toLowerCase();
        if (hh.startsWith("-")) {
            hh = hh.substring(1);
        }
        StringBuilder sb = new StringBuilder("");
        int j = 0;
        for (int i = 0; i < hh.length(); i++) {
            if (j == 2) {
                sb.append("/");
                j = 0;
            }
            sb.append(hh.charAt(i));
            j++;
        }
        return sb.toString();
    }

    public ObjectCache getObjectCache(HashValue dump, boolean createIfNotFound) {
        if (!isEnabled()) {
            return null;
        }
//        DumpPath dp=new DumpPath(dump);
//        File f = getDumpFolder(dump, createIfNotFound);
//        return f == null ? null : new MomCache(new DumpPath(dump), this);
        return new ObjectCache(new DumpPath(dump.getValue()), this);
    }

//    public ObjectCache getObjectCache(Dumpable d, boolean createIfNotFound) {
//        if (!isEnabled()) {
//            return null;
//        }
//        return getObjectCache(d.dump(), createIfNotFound);
//    }

    public DumpCacheFile getFile(DumpPath dump, String path) {
        return new DumpCacheFile(
                dump, path, new HFile(getDumpFolder(dump, true), path), this
        );
    }

    public HFile getDumpFolder(String dump, boolean createIfNotFound) {
        return getDumpFolder(new DumpPath(dump), createIfNotFound);
    }

    public HFile getDumpFolder(DumpPath dumpObj, boolean createIfNotFound) {
        String dump = dumpObj.getDump();
        HFile r = new HFile(getRepositoryFolder(), dumpObj.getPath());
        ObjectCache d = null;
        try {
            d = loadCache(r);
        } catch (IOException e) {
            //
        }
        boolean enabled = isEnabled();
        if (d == null) {
            if (createIfNotFound && enabled) {
                r.mkdirs();
                PrintStream fos = null;
                try {
                    HFile file = new HFile(r, dumpFileName);
                    fos = new PrintStream(file.getOutputStream());
                    fos.print(dump);
//                    System.out.println(file + " : stored dump");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    fos.close();
                }
                return r;
            }
        } else if (dump.equals(d.getDump())) {
            return r;
        }

        HFile[] f = r.listFiles();
        int min = 0;
        if (f != null) {
            for (HFile subFolder : f) {
                if (subFolder.isDirectory()) {
                    d = null;
                    try {
                        d = loadCache(subFolder);
                    } catch (IOException e) {
                        //
                    }
                    if (d != null) {
                        if (dump.equals(d.getDump())) {
                            return subFolder;
                        }
                        int x = 0;
                        try {
                            x = Integer.parseInt(subFolder.getName());
                        } catch (NumberFormatException e) {
                            //
                        }
                        if (x > min) {
                            min = x;
                        }
                    }
                }
            }
        }
        if (createIfNotFound && enabled) {
            HFile n = new HFile(r, String.valueOf(min + 1));
            n.mkdirs();
            PrintStream fos = null;
            try {
                HFile file = new HFile(n, dumpFileName);
                fos = new PrintStream(file.getOutputStream());
                fos.print(dump);
//                System.out.println(file + " : stored dump");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                fos.close();
            }
            return n;
        }
        return null;
    }

    public Iterator<ObjectCache> iterate() {
        return new ObjectCacheIterator();
    }


    public CacheMode getEffectiveMode() {
        CacheMode m = mode == null ? CacheMode.INHERITED : mode;
        CacheMode p = parent == null ? (Maths.Config.getPersistenceCacheMode()) : parent.getEffectiveMode();
        if (m == CacheMode.DISABLED || p == CacheMode.DISABLED) {
            return CacheMode.DISABLED;
        }
        if (m == CacheMode.INHERITED) {
            return p;
        }
        switch (p) {
            case READ_ONLY: {
                switch (m) {
                    case ENABLED: {
                        return CacheMode.READ_ONLY;
                    }
                    case READ_ONLY: {
                        return CacheMode.READ_ONLY;
                    }
                    case WRITE_ONLY: {
                        return CacheMode.DISABLED;
                    }
                }
                return CacheMode.DISABLED;
            }
            case WRITE_ONLY: {
                switch (m) {
                    case ENABLED: {
                        return CacheMode.WRITE_ONLY;
                    }
                    case READ_ONLY: {
                        return CacheMode.DISABLED;
                    }
                    case WRITE_ONLY: {
                        return CacheMode.WRITE_ONLY;
                    }
                }
                return CacheMode.DISABLED;
            }
            case ENABLED: {
                return m;
            }
        }
        return CacheMode.DISABLED;
    }

    public boolean isEnabled() {
        return getEffectiveMode() != CacheMode.DISABLED;
    }

    public CacheMode getMode() {
        return mode;
    }

    public PersistenceCache setEnabled(boolean enabled) {
        this.mode = enabled ? CacheMode.ENABLED : CacheMode.DISABLED;
        return this;
    }

    public PersistenceCache setMode(CacheMode mode) {
        this.mode = mode == null ? CacheMode.INHERITED : mode;
        return this;
    }


    public boolean clear() {
        System.err.println("Warning, clearing " + getRepositoryFolder().getPath() + " started.");
        boolean b = getRepositoryFolder().deleteFolderTree(cacheFileFilter, FailStrategy.FAIL_SAFE);
        if (!b) {
            System.err.println("Warning, clearing " + getRepositoryFolder().getPath() + " failed.");
        }
        return b;
    }

    @Override
    public void setCacheBaseFolder(HFile rootFolder) {
        setRootFolder(rootFolder);
    }

    public PersistenceCache setRootFolder(HFile rootFolder) {
        this.rootFolder = rootFolder;
        this.rootFolderCached = null;
        return this;
    }


    public PersistenceCache setRootFolder(String rootFolder) {
        return setRootFolder(rootFolder == null ? null : IOUtils.createHFile((rootFolder)));
    }

    public boolean isLogLoadStatsEnabled() {
        return logLoadStatsEnabled;
    }

    public PersistenceCache setLogLoadStatsEnabled(boolean logLoadStatsEnabled) {
        this.logLoadStatsEnabled = logLoadStatsEnabled;
        return this;
    }

    public long getTaskTimeThreshold() {
        return taskTimeThreshold;
    }

    public PersistenceCache setTaskTimeThreshold(long taskTimeThreshold) {
        this.taskTimeThreshold = taskTimeThreshold;
        return this;
    }

    private class ObjectCacheIterator implements Iterator<ObjectCache> {

        ObjectCache current;
        Stack<HFile> currentFiles = new Stack<HFile>();

        public ObjectCacheIterator() {
            Stack<HFile> stack = new Stack<HFile>();
            stack.push(rootFolder);
            while (!stack.isEmpty()) {
                HFile ff = stack.pop();
                HFile file1 = new HFile(ff, dumpFileName);
                if (file1.existsOrWait()) {
                    currentFiles.push(ff);
                }
                HFile[] files = ff.listFiles();
                if (files != null) {
                    for (HFile file : files) {
                        if (file.isDirectory()) {
                            stack.push(file);
                        }
                    }
                }
            }
        }

        public boolean hasNext() {
            if (currentFiles.isEmpty()) {
                return false;
            }
            while (!currentFiles.isEmpty()) {
                HFile folder = currentFiles.pop();
                current = null;
                try {
                    current = loadCache(folder);
                } catch (Throwable e) {
                    //
                }
                if (current != null) {
                    return true;
                }
            }
            return false;
        }

        public ObjectCache next() {
            return current;
        }

        public void remove() {
            //
        }
    }

    public boolean isIgnorePrevious() {
        return ignorePrevious;
    }

    public PersistenceCache setIgnorePrevious(boolean ignorePrevious) {
        this.ignorePrevious = ignorePrevious;
        return this;
    }

    public PathFS getFS() {
        return pathFS;
    }

    public PersistenceCache setAll(PersistenceCache other) {
        if (other != null) {
            mode = other.mode;
            rootFolder = other.rootFolder;
            logLoadStatsEnabled = other.logLoadStatsEnabled;
            ignorePrevious = other.ignorePrevious;
        }
        return this;
    }

//    public <T> T evaluate(String cacheItemName, T oldValue, final Evaluator evaluator, final Object args) {
//        return evaluate(cacheItemName, oldValue, evaluator, toObjArr(args));
//    }

    public <T> T evaluate(final String cacheItemName, ProgressMonitor monitor, final Evaluator evaluator, final Object... args) {
        return evaluate(cacheItemName, monitor,new Evaluator2() {
            @Override
            public void init() {
                for (Method m : evaluator.getClass().getDeclaredMethods()) {
                    Init initMethod = m.getAnnotation(Init.class);
                    if (initMethod != null) {
                        if (m.getParameterTypes().length != 0) {
                            System.err.println("Ignored method " + m + ". Too many arguments");
                        } else {
                            m.setAccessible(true);
                            try {
                                m.invoke(evaluator);
                            } catch (IllegalAccessException e) {
                                throw new IllegalArgumentException(e);
                            } catch (InvocationTargetException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    }
                }
            }

            @Override
            public Object evaluate(Object[] args) {
                return evaluator.evaluate(args);
            }
        }, args);
    }

    /**
     * retrieve cache get named <code>cacheItemName</code> from cache if already evaluated, or reevaluate it and store to cache
     *
     * @param cacheItemName cache get name to evaluated
     * @param evaluator     evaluator invoked when value could not be retrieved
     * @param args          ALL arguments needed for evaluation. This argument should include all global and inherited values on which depends evaluation.
     *                      If this arguments are wrong, cache may return a value evaluated for other parameters.
     *                      All arguments should be Dumpable (@see {@link Maths#dump(Object)})
     * @param <T>           cache get type/class
     * @return oldValue if not null, or loaded cached if already evaluated or reevaluate it at call time
     */
    public <T> T evaluate(final String cacheItemName, ProgressMonitor monitor, final Evaluator2 evaluator, final Object... args) {
        Dumper dump = new Dumper();
        for (Object arg : args) {
            dump.add(arg);
        }

        final ObjectCache objCache = getObjectCache(new HashValue(dump.toString()), true);
        return evaluate(objCache, cacheItemName, monitor,evaluator, args);
    }

    /**
     * retrieve cache get named <code>cacheItemName</code> from cache if already evaluated, or reevaluate it and store to cache
     *
     * @param cacheItemName cache get name to evaluated
     * @param evaluator     evaluator invoked when value could not be retrieved
     * @param args          ALL arguments needed for evaluation. This argument should include all global and inherited values on which depends evaluation.
     *                      If this arguments are wrong, cache may return a value evaluated for other parameters.
     *                      All arguments should be Dumpable (@see {@link Maths#dump(Object)})
     * @param <T>           cache get type/class
     * @return oldValue if not null, or loaded cached if already evaluated or reevaluate it at call time
     */
    public <T> T evaluate(final ObjectCache objCache, final String cacheItemName, ProgressMonitor monitor, final Evaluator2 evaluator, final Object... args) {
        return objCache.getObjectCacheFile(cacheItemName).getLock().invoke(
                PersistenceCache.LOCK_TIMEOUT,
                new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        T oldValue = null;
                        CacheMode cacheMode = getEffectiveMode();
                        boolean cacheEnabled = isEnabled();
                        long timeThresholdMilli = getTaskTimeThreshold();
                        if (cacheEnabled && cacheMode != CacheMode.WRITE_ONLY) {
                            Chronometer c = new Chronometer();
                            c.start();
                            try {
                                oldValue = (T) objCache.load(cacheItemName, null);
                                log.log(Level.FINE, "[PersistenceCache] " + cacheItemName + " loaded from disk in "+c);
                            } catch (Exception e) {
                                log.log(Level.SEVERE, "[PersistenceCache] " + cacheItemName + " throws an error when reloaded from disk. Cache ignored (" + e + ")");
                                //
                            }
                            c.stop();
                            if (timeThresholdMilli > 0 && c.getTime() > timeThresholdMilli*1000000) {
                                log.log(Level.WARNING, "[PersistenceCache] " + cacheItemName + " loading took too long (" + c + " > " + Chronometer.formatPeriodMilli(timeThresholdMilli) + ")");
                            }
                        }
                        if (oldValue == null) {
                            evaluator.init();
                            Chronometer c = new Chronometer();
                            c.start();
                            oldValue = (T) evaluator.evaluate(args);
                            c.stop();
                            if (objCache != null && cacheEnabled && cacheMode != CacheMode.READ_ONLY) {
                                long computeTime = c.getTime();
                                if (computeTime >= minimumTimeForCache) {
                                    c.start();
                                    objCache.store(cacheItemName, oldValue,monitor);
                                    c.stop();
                                    log.log(Level.SEVERE, "[PersistenceCache] " + cacheItemName + " evaluated in "+c+" ; stored to disk in "+c);
                                    objCache.addStat(cacheItemName, computeTime);
                                    objCache.addStat(cacheItemName + "#storecache", c.getTime());
                                    if (isLogLoadStatsEnabled()) {
                                        c.start();
                                        try {
                                            oldValue = (T) objCache.load(cacheItemName, null);
                                        } catch (Exception e) {
                                            //
                                        }
                                        c.stop();
                                        objCache.addStat(cacheItemName + "#loadcache", c.getTime());
                                        if (timeThresholdMilli > 0 && c.getTime() > timeThresholdMilli*1000000) {
                                            log.log(Level.WARNING, "[PersistenceCache] " + cacheItemName + " reloading took too long (" + c + " > " + Chronometer.formatPeriodMilli(timeThresholdMilli) + ")");
                                        }
                                    }
                                }
                            }
                        }
                        return oldValue;
                    }
                }
        );

    }

//    private Object[] toObjArr(Object args) {
//        return (args instanceof Object[]) ? ((Object[]) args) : new Object[]{args};
//    }

//    public <T> T evaluate(String key, final Evaluator evaluator, final Object args) {
//        return evaluate(key, evaluator, toObjArr(args));
//    }

//    public <T> T evaluate(String key, final Evaluator evaluator, final Object... args) {
//        return evaluate(key, null, evaluator, args);
//    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public PersistenceCache setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public PersistenceCache setRepositoryFolder(HFile repositoryFolder) {
        this.repositoryFolder = repositoryFolder;
        return this;
    }


    public <T> T getOrNull(final HashValue dump, final String cacheItemName) {
        T value = null;
        if (isEnabled()) {
            ObjectCache objCache = getObjectCache(dump, true);
            value = objCache.getObjectCacheFile(cacheItemName).getLock().invoke(
                    PersistenceCache.LOCK_TIMEOUT,
                    new Callable<T>() {
                        @Override
                        public T call() throws Exception {
                            T value = null;
                            ObjectCache momCache = null;
                            long timeThresholdMilli = getTaskTimeThreshold();
                            boolean cacheEnabled = isEnabled();
                            CacheMode cacheMode = getEffectiveMode();
                            if (cacheEnabled && cacheMode != CacheMode.WRITE_ONLY) {
                                momCache = getObjectCache(dump, true);
                                Chronometer c = new Chronometer();
                                c.start();
                                try {
                                    value = (T) momCache.load(cacheItemName, null);
                                } catch (Exception e) {
                                    System.err.println(e);
                                    //
                                }
                                c.stop();
                                if (timeThresholdMilli > 0 && c.getTime() > timeThresholdMilli*1000000) {
                                    System.out.println("[PersistenceCache] " + cacheItemName + " loading took too long (" + c + " > " + Chronometer.formatPeriodMilli(timeThresholdMilli) + ")");
                                }
                            }
                            return value;
                        }
                    });
        }
        return value;
    }

    public boolean isCached(HashValue dump, String cacheItemName) {
        if (isEnabled()) {

            ObjectCache objCache = null;
            boolean cacheEnabled = isEnabled();
            CacheMode cacheMode = getEffectiveMode();
            if (cacheEnabled && cacheMode != CacheMode.WRITE_ONLY) {
                objCache = getObjectCache(dump, true);
                try {
                    if (objCache.exists(cacheItemName)) {
                        return true;
                    }
                    return objCache.getObjectCacheFile(cacheItemName).getLock().isLocked();
                } catch (Exception e) {
                    System.err.println(e);
                    //
                }
            }
        }
        return false;
    }


}
