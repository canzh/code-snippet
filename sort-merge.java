class CodeSnippet {
    public void mergeSort(int[] nums) {
        sort(nums, 0, nums.length - 1);
    }

    // Main function that sorts arr[l..r] using merge()
    void sort(int[] nums, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            sort(nums, left, mid);
            sort(nums, mid + 1, right);

            merge(nums, left, mid, right);
        }
    }

    private void merge(int[] nums, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];

        // i for left half, j for the other half, k for merged array
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (nums[i] < nums[j]) {
                temp[k] = nums[i];
                i++;
            } else {
                temp[k] = nums[j];
                j++;
            }
            k++;
        }

        while (i <= mid) {
            temp[k] = nums[i];
            i++;
            k++;
        }

        while (j <= right) {
            temp[k] = nums[j];
            j++;
            k++;
        }

        // copy sorted nums back
        for (int m = left; m <= right; m++) {
            nums[m] = temp[m - left];
        }
    }

}