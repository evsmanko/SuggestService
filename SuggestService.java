import java.util.*;


public class SuggestService {

    private final TrieNode root = new TrieNode(null, '0');

    /*
     * Для быстрого поиска подсказок использую префиксное дерево.
     * Ноды в дереве двунаправленные, чтобы быстро искать полное слово с конца.
     * leaf указывает не на листья дерева, а на конец слова
     * (чтобы правильно обработать запросы с одинаковым началом)
     * */
    private static class TrieNode {
        TrieNode parent;
        Map<Character, TrieNode> children = new TreeMap<>();
        boolean isEndOfWord;
        char c;

        TrieNode(TrieNode parent, char c) {
            this.parent = parent;
            this.c = c;
        }
    }

    //Поднимаемся вверх по нодам, чтобы получить полное слово
    private String getStringByNode(TrieNode node) {
        Deque<String> t = new LinkedList<>();
        while (node.parent != null) {
            t.push(String.valueOf(node.c));
            node = node.parent;
        }
        return String.join("", t);
    }

    public SuggestService(List<String> companyNames) {
        for (String name : companyNames) {
            TrieNode v = root;
            for (char ch : name.toLowerCase().toCharArray()) {
                if (!v.children.containsKey(ch)) {
                    v.children.put(ch, new TrieNode(v, ch));
                }
                v = v.children.get(ch);
            }
            v.isEndOfWord = true;
        }

    }

    public List<String> suggest(String input, Integer numberOfSuggest) {
        List<String> list = new ArrayList<>();
        TrieNode currentNode = root;
        for (Character ch : input.toLowerCase().toCharArray()) {
            if (currentNode.children.containsKey(ch)) {
                currentNode = currentNode.children.get(ch);
            } else return list;
        }
        Deque<TrieNode> notVisited = new LinkedList<>(currentNode.children.values());
        while (numberOfSuggest > 0 && !notVisited.isEmpty()) {
            TrieNode toVisit = notVisited.pop();
            toVisit.children.values().forEach(notVisited::push);
            if (toVisit.isEndOfWord) {
                list.add(getStringByNode(toVisit));
                numberOfSuggest -= 1;
            }
        }
        return list;
    }
}
