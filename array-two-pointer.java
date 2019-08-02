public class CodeSnippet {
    // color defined as 0, 1, 2
    // notice the move of indices
    public void sortColors(int[] nums) {
        if (nums == null || nums.length == 0)
            return;

        int low = 0;
        int high = nums.length - 1;

        for (int i = low; i <= high;) {
            if (nums[i] == 0) {
                swap(nums, i, low);
                low++;
                i++;
            } else if (nums[i] == 2) {
                swap(nums, i, high);
                high--;
            } else {
                i++;
            }
        }
    }

    private void swap(int[] nums, int low, int mid) {
        int temp = nums[low];
        nums[low] = nums[mid];
        nums[mid] = temp;
    }
}