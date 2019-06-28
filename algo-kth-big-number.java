
public class CodeSnippet {
    public int kthNumber(int[] arr, int k) {
        if (k < 0 || k > arr.length) {
            throw new Exception();
        }

        int left = 0;
        int right = arr.length;
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
}