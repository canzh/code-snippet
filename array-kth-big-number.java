
public class CodeSnippet {
    public int kthNumber(int[] arr, int k) {
        if (k < 0 || k > arr.length) {
            throw new Exception();
        }

        int left = 0;
        int right = arr.length - 1;
        int idx = partition(arr, left, right);

        while (idx != k - 1) {
            if (idx > k - 1) {
                right = idx - 1;
                idx = partition(arr, left, right);
            } else if (idx < k - 1) {
                left = idx + 1;
                idx = partition(arr, left, right);
            }
        }

        return arr[idx];
    }

    // more concise
    public int findKthLargest(int[] nums, int k) {
        k = nums.length - k;
        int lo = 0;
        int hi = nums.length - 1;
        while (lo < hi) {
            final int j = partition(nums, lo, hi);
            if (j < k) {
                lo = j + 1;
            } else if (j > k) {
                hi = j - 1;
            } else {
                break;
            }
        }
        return nums[k];
    }

    private int partition(int[] a, int lo, int hi) {

        int i = lo;
        int j = hi + 1;
        while (true) {
            while (i < hi && less(a[++i], a[lo]))
                ;
            while (j > lo && less(a[lo], a[--j]))
                ;
            if (i >= j) {
                break;
            }
            exch(a, i, j);
        }
        exch(a, lo, j);
        return j;
    }

    private void exch(int[] a, int i, int j) {
        final int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    private boolean less(int v, int w) {
        return v < w;
    }
}