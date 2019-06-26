import java.util.*;

public class CodeSnippet {
    List<Integer>[] adj;
    int size;

    private void dfs(int v, boolean[] visited) {
        visited[v] = true;

        // process visit logic

        for(Integer neigbor : adj[v]) {
            if(!visited[neigbor]) {
                dfs(neigbor, visited);
            }
        }
    }

    public void dfs() {
        boolean[] visited = new boolean[];

        for(int i =0;i<size;i++){
            if(!visited[i]) {
                dfs(i, visited);
            }
        }
    }
}