import java.util.*;

public class CodeSnippet {
    List<Integer>[] adj;
    int size;

    public void bfs(int startPoint) {
        boolean[] visited = new boolean[size];
        Queue<Integer> queue = new LinkedList<Integer>();

        visited[startPoint] = true;
        queue.offer(startPoint);

        while (!queue.isEmpty()) {
            Integer n = queue.poll();

            for (Integer neigbor : adj[n]) {
                if (!visited[neigbor]) {
                    visited[neigbor] = true;
                    queue.offer(neigbor);

                    // process visit logic
                }
            }
        }
    }
}