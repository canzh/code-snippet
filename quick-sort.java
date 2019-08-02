import java.util.*;

public class CodeSnippet {
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // pivot choose can be many ways, here use the high

        // if choose high as pivot, so we do not need to swap it to the last position

        int small = low - 1;

        for (int i = low; i < high; i++) { // not <= high, since high is for pivot
            if (arr[i] <= pivot) {
                small++;

                swap(arr, small, i);
            }
        }

        swap(arr, small + 1, high);

        return small + 1;
    }

    int partition2(int[] nums, int low, int high) {
        int pivot = nums[high];
        int small = low;

        for (int i = low; i < high; i++) {
            if (nums[i] <= pivot) {
                swap(nums, small, i);
                small++;
            }
        }

        swap(nums, small, high);

        return small;
    }

    private void swap(int[] arr, int p1, int p2) {
        int temp = arr[p1];
        arr[p1] = arr[p2];
        arr[p2] = temp;
    }

    private void quickSortRecursive(int[] arr, int low, int high) {
        if (low < high) {
            int index = partition(arr, 0, arr.length - 1);

            quickSortRecursive(arr, low, index - 1);
            quickSortRecursive(arr, index + 1, high);
        }
    }

    private void quickSortIterative(int[] arr) {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        stack.push(arr.length - 1);

        while (!stack.isEmpty()) {
            int high = stack.pop();
            int low = stack.pop();

            int idx = partition(arr, low, high);

            if (idx - 1 > low) {
                stack.push(low);
                stack.push(idx - 1);
            }

            if (idx + 1 < high) {
                stack.push(idx + 1);
                stack.push(high);
            }
        }
    }

    // In 3 Way QuickSort, an array arr[l..r] is divided in 3 parts:
    // a) arr[l..i] elements less than pivot.
    // b) arr[i+1..j-1] elements equal to pivot.
    // c) arr[j..r] elements greater than pivot.

    // optimize:
    // 1. pivot
    // 2. smaller side, larger side
    // 3. threshold for smaller size to use insertion sort
    // 4. equal values
}