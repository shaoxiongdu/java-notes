package positive;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版权所有 2021 @ ShaoxiongDu 保留所有权利
 */
public class MySet {

    //产生聚合关系
    private Set set = new HashSet();

    private int addCount = 0;

    public boolean add(Object o){
        addCount++;
        return set.add(o);
    }

    public int getAddCount() {
        return addCount;
    }
}
