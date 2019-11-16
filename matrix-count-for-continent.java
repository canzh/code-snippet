public class Code {
    public int numbers(int[][] data) {
        int n = data.length;
        boolean[] visited = new boolean[n];

        int ans = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(data, i, visited);
                ans++;
            }
        }

        return ans;
    }

    public void dfs(int[][] data, int v, boolean[] visited) {
        visited[v] = true;

        for (int i = 0; i < data.length; i++) {
            if (data[v][i] == 1 && !visited[i]) {
                dfs(data, i, visited);
            }
        }
    }

    public static void main(String[] args) {
        int[][] data = {
                {1, 0, 1, 1},
                {0, 1, 1, 0},
                {1, 1, 1, 0},
                {1, 0, 0, 1}
        };

        Code t = new Code();
        int a = t.numbers(data);

        System.out.println(a);
    }

    // following is WRONG
    public int count(int[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0)
            return 0;

        int m = data.length;
        int n = data[0].length;

        boolean[][] visited = new boolean[m][n];
        int ans = 0;

        for (int i = 0; i < m; i++) {
            for (int j = i; j < n; j++) {
                if (visit(data, i, j, visited)) {
                    ans++;
                }
            }
        }

        return ans;
    }

    boolean visit(int[][] data, int r, int c, boolean[][] visited) {
        int m = data.length;
        int n = data[0].length;

        if (r < 0 || r >= m || c < 0 || c >= n || visited[r][c] || data[r][c] == 0)
            return false;

        visited[r][c] = true;

        visit(data, r, c + 1, visited);
        visit(data, r + 1, c, visited);

        return true;
    }
}