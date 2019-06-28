import java.util.*;

public class CodeSnippet {
    List<Integer>[] adj;
    int size;

    public boolean bfs(int src, int dest, int[] predecessor, int[] distance) {
        boolean[] visited = new boolean[size];
        Queue<Integer> queue = new LinkedList<Integer>();

        distance[src] = 0;
        visited[src] = true;
        queue.offer(src);

        while (!queue.isEmpty()) {
            Integer n = queue.poll();

            for (Integer neigbor : adj[n]) {
                if (!visited[neigbor]) {
                    visited[neigbor] = true;
                    queue.offer(neigbor);

                    distance[neigbor] = distance[n] + 1;
                    predecessor[neigbor] = n;

                    // found
                    if (neigbor == dest) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void printShortestPath(int src, int dest) {
        int[] predecessor = new int[size];
        int[] distance = new int[size];

        for (int i = 0; i < size; i++) {
            distance[i] = Integer.MAX_VALUE;
            predecessor[i] = -1;
        }

        if (!bfs(src, dest, predecessor, distance)) {
            // src and dest are not connected
            return;
        }

        List<Integer> path = new LinkedList<>();
        int crawl = dest;
        path.add(crawl);

        while (predecessor[crawl] != -1) {
            path.add(predecessor[crawl]);
            crawl = predecessor[crawl];
        }

        // shortest path length is distance[dest]

        // path is:
        Collections.reverse(path);
    }
}