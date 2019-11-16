/*
 * <p>
 * 优先队列的实现（基于堆）
 * <p>
 * 问题：  //items = new Item[5];    为什么这样写会报错
 */
public class MaxPQ<Item extends Comparable<Item>> {

    private int N; // 堆中元素的个数
    private int maxN;
    private Item[] items; // 存储完全二叉树的数组

    public MaxPQ(int maxN) {
        this.maxN = maxN;
        items = (Item[]) new Comparable[maxN + 1];
        // items = new Item[5];
    }

    // 删除最大元素
    public Item delMax() {

        if (isEmpty())
            return null;
        Item max = items[1];
        items[1] = items[N--];
        items[N + 1] = null;
        sink(1);

        return max;
    }

    // 插入元素
    public void insert(Item item) {
        if (item == null)
            throw new NullPointerException("插入元素为空！");
        /*
         * if(N >= maxN){ if (less(items[N],item)) items[N] = item; }else{ items[++N] =
         * item; }
         */
        items[++N] = item;
        swim(N);
        // printAr();
    }

    private boolean less(Item item, Item item1) {
        return item.compareTo(item1) < 0;
    }

    // 上浮
    private void swim(int k) {
        while (k > 1) {
            if (less(k / 2, k))
                swap(k, k / 2);
            k /= 2;
        }

        // 其实这样写更好
        /*
         * while(k > 1 && less(k / 2,k)){ swap(k,k / 2); k /= 2; }
         */
    }

    // 下沉
    private void sink(int k) {
        while (2 * k <= N) {
            int j = 2 * k;
            if (j < N && less(j, j + 1))
                j++;
            if (!less(k, j))
                break;
            swap(k, j);
            k = j;
        }
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    /**
     * 几个辅助函数
     */

    private void swap(int k, int i) {
        Item tmp = items[k];
        items[k] = items[i];
        items[i] = tmp;
    }

    private boolean less(int i, int k) {
        return items[i].compareTo(items[k]) < 0;
    }

    public void printAr() {
        System.out.println(Arrays.toString(items));
    }
}