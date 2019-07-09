import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashNodeLocator {

    static class MemcachedNode {
        String ip;
        String port;

        String getSocketAddress() {
            return ip + port;
        }
    }

    interface HashAlgorithm {
        long hash(String k);
    }

    private final static int VIRTUAL_NODE_SIZE = 12;
    private final static String VIRTUAL_NODE_SUFFIX = "-";
    private volatile TreeMap<Long, MemcachedNode> hashRing;
    private final HashAlgorithm hashAlg;

    public ConsistentHashNodeLocator(List<MemcachedNode> nodes, HashAlgorithm hashAlg) {
        this.hashAlg = hashAlg;
        this.hashRing = buildConsistentHashRing(hashAlg, nodes);
    }

    // Public API
    public MemcachedNode getPrimary(String k) {
        long hash = hashAlg.hash(k);
        return getNodeForKey(hashRing, hash);
    }

    private MemcachedNode getNodeForKey(TreeMap<Long, MemcachedNode> hashRing, long hash) {

        /* 向右找到第一个key */
        Map.Entry<Long, MemcachedNode> locatedNode = hashRing.ceilingEntry(hash);

        /* 想象成为一个环，超出尾部取出第一个 */
        if (locatedNode == null) {
            locatedNode = hashRing.firstEntry();
        }

        return locatedNode.getValue();
    }

    private TreeMap<Long, MemcachedNode> buildConsistentHashRing(HashAlgorithm hashAlgorithm, List<MemcachedNode> nodes) {

        TreeMap<Long, MemcachedNode> virtualNodeRing = new TreeMap<>();

        for (MemcachedNode node : nodes) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                // 新增虚拟节点的方式如果有影响，也可以抽象出一个由物理节点扩展虚拟节点的类
                virtualNodeRing.put(hashAlgorithm.hash(node.getSocketAddress().toString() + VIRTUAL_NODE_SUFFIX + i), node);
            }
        }

        return virtualNodeRing;
    }
}
