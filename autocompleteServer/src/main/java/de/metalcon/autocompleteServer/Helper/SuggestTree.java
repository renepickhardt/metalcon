package de.metalcon.autocompleteServer.Helper;

/*
 * Copyright 2011-2013 Nicolai Diethelm
 *
 * This software is free software. You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

//package net.sourceforge.suggesttree;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Random;

/**
 * An efficient data structure for rank-sensitive autocomplete features. It
 * stores a set of suggestion strings with associated weights and returns for
 * any given prefix a rank-ordered list of the top <i>k</i> highest weighted
 * suggestions that start with that prefix. It also provides methods to insert a
 * new suggestion, modify the weight of a suggestion, or remove a suggestion.
 * <p>
 * The basic structure is a compressed ternary search tree of the suggestions,
 * where nodes (prefixes) with the same completions are merged into one node,
 * and where each node corresponding to a suggestion stores the weight of the
 * suggestion and a reference to the suggestion string. Only the first character
 * of a node is stored explicitly; the other characters are read from the
 * corresponding suggestion string or (if the node does not correspond to a
 * suggestion) from a suggestion string that is referenced instead. In addition
 * to that basic structure, each node in the tree holds a precomputed
 * "suggestion list": a rank-ordered array of references to the nodes of the top
 * <i>k</i> highest weighted suggestions that start with prefix corresponding to
 * the node.
 * <p>
 * For each suggestion inserted into the tree, at most one new node is added and
 * at most one existing node is split into two nodes. A tree with <i>n</i>
 * suggestions has thus at most 2<i>n</i> - 1 nodes. But what is the total
 * length of the suggestion lists in the tree? The answer is easier when we do
 * not look at a ternary search tree but at a simpler trie data structure where
 * the child nodes of a node are not arranged as a binary search tree. If such a
 * tree has 2<i>n</i> - 1 nodes, then each internal node has exactly two child
 * nodes. Consequently, if all leaf nodes are at the same depth, the tree has
 * <i>n</i> suggestion lists of length 1, <i>n</i>/2 suggestion lists of length
 * 2, <i>n</i>/4 suggestion lists of length 4, and so on until the maximum list
 * length of <i>k</i> is reached. Assuming <i>k</i> is a power of two, this
 * gives a total list length of <i>n</i> + 2(<i>n</i>/2) + 4(<i>n</i>/4) + ... +
 * <i>k</i>(<i>n</i>/<i>k</i>) + <i>k</i>(<i>n</i>/<i>k</i> - 1), which is
 * approximately (log<sub>2</sub><i>k</i> + 2) <i>n</i>.
 * <p>
 * Ternary search trees are much less sensitive to insertion order than binary
 * search trees. Even in the worst case, when the suggestions are inserted into
 * the tree in lexicographic order, performance is usually only slightly
 * degraded. The reason for this is that not the entire tree structure
 * degenerates into a linked list, only each of the small binary search trees
 * within the ternary search tree does. However, for best performance, the
 * suggestions should be inserted into the tree in random order. For large
 * <i>n</i>, this practically always produces a balanced tree where going left
 * or right cuts the search space more or less in half.
 * <p>
 * If a suggestion is removed and the corresponding node has no middle child but
 * a left child and a right child, the node is replaced with either the leftmost
 * node from its right subtree or the rightmost node from its left subtree. To
 * preserve the balance of the tree, the choice is made at random.
 * <p>
 * This implementation is not synchronized. If multiple threads access a tree
 * concurrently, and at least one of the threads modifies the tree, it must be
 * synchronized externally. This is typically accomplished by synchronizing on
 * some object that naturally encapsulates the tree.
 * 
 * @version 1 August 2013
 */
public class SuggestTree {
    
    private final Random random = new Random();
    private final int k;
    private Node root;
    private int size;

    /**
     * Creates a tree that returns the top {@code k} highest weighted
     * autocomplete suggestions for a given prefix.
     * @throws IllegalArgumentException if the specified {@code k} value is less
     * than 1
     */
    public SuggestTree(int k) {
        if(k < 1)
            throw new IllegalArgumentException();
        this.k = k;
        root = null;
        size = 0;
    }
    
    /**
     * Returns the number of suggestions in this tree.
     */
    public int size() {
        return size;
    }
    
    /**
     * Removes all of the suggestions from this tree.
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Returns the node with the top <i>k</i> highest weighted suggestions in
     * this tree that start with the specified prefix, or returns {@code null}
     * if the tree contains no suggestion with the prefix.
     * @throws IllegalArgumentException if the specified prefix is an empty
     * string
     * @throws NullPointerException if the specified prefix is {@code null}
     */
    public Node getBestSuggestions(String prefix) {
        if(prefix.isEmpty())
            throw new IllegalArgumentException();
        int i = 0;
        Node n = root;
        while(n != null) {
            if(prefix.charAt(i) < n.firstChar)
                n = n.left;
            else if(prefix.charAt(i) > n.firstChar)
                n = n.right;
            else{
                for(i++; i < n.charEnd && i < prefix.length(); i++) {
                    if(prefix.charAt(i) != n.suggestion.charAt(i))
                        return null;
                }
                if(i < prefix.length())
                    n = n.mid;
                else
                    return n;
            }
        }
        return null;
    }

    /**
     * Returns the weight of the specified suggestion in this tree, or -1 if the
     * tree does not contain the suggestion.
     * @throws NullPointerException if the specified suggestion is {@code null}
     */
    public int weightOf(String suggestion) {
        Node n = getNode(suggestion);
        return (n != null) ? n.weight : -1;
    }
    
    private Node getNode(String suggestion) {
        if(suggestion.isEmpty())
            return null;
        int i = 0;
        Node n = root;
        while(n != null) {
            if(suggestion.charAt(i) < n.firstChar)
                n = n.left;
            else if(suggestion.charAt(i) > n.firstChar)
                n = n.right;
            else{
                for(i++; i < n.charEnd; i++) {
                    if(i == suggestion.length()
                            || suggestion.charAt(i) != n.suggestion.charAt(i))
                        return null;
                }
                if(i < suggestion.length())
                    n = n.mid;
                else
                    return (n.weight != -1) ? n : null;
            }
        }
        return null;
    }
    
    /**
     * Inserts the specified suggestion with the specified weight into this
     * tree, or assigns the specified new weight to the suggestion if it is
     * already present.
     * @throws IllegalArgumentException if the specified suggestion is an empty
     * string or the specified weight is negative
     * @throws NullPointerException if the specified suggestion is {@code null}
     */
    public void put(String suggestion, int weight, String key) {
        if(suggestion.isEmpty() || weight < 0)
            throw new IllegalArgumentException();
        if(root == null) {
            root = new Node(suggestion, weight, key, 0, null);
            size++;
            return;
        }
        int i = 0;
        Node n = root;
        while(true) {
            if(suggestion.charAt(i) < n.firstChar) {
                if(n.left != null)
                    n = n.left;
                else{
                    n.left = new Node(suggestion, weight, key, i, n);
                    insertIntoLists(n.left);
                    size++;
                    return;
                }
            }else if(suggestion.charAt(i) > n.firstChar) {
                if(n.right != null)
                    n = n.right;
                else{
                    n.right = new Node(suggestion, weight, key, i, n);
                    insertIntoLists(n.right);
                    size++;
                    return;
                }
            }else{
                for(i++; i < n.charEnd; i++) {
                    if(i == suggestion.length()
                            || suggestion.charAt(i) != n.suggestion.charAt(i)) {
                        n = splitNode(n, i);
                        break;
                    }
                }
                if(i < suggestion.length()) {
                    if(n.mid != null)
                        n = n.mid;
                    else{
                        n.mid = new Node(suggestion, weight, key, i, n);
                        insertIntoLists(n.mid);
                        size++;
                        return;
                    }
                }else if(n.weight == -1) {
                    n.suggestion = suggestion;
                    n.weight = weight;
                    insertIntoLists(n);
                    size++;
                    return;
                }else if(weight > n.weight) {
                    n.weight = weight;
                    updateListsIncreasedWeight(n);
                    return;
                }else if(weight < n.weight) {
                    n.weight = weight;
                    updateListsDecreasedWeight(n);
                    return;
                }else
                    return;
            }
        }
    }
    
    private Node splitNode(Node n, int position) {
        Node[] list = (n.list.length < k) ? n.list : Arrays.copyOf(n.list, k);
        Node m = new Node(list, n, position);
        n.firstChar = n.suggestion.charAt(position);
        if(n.left != null)
            n.left.parent = m;
        n.left = null;
        if(n.right != null)
            n.right.parent = m;
        n.right = null;
        if(n == root)
            root = m;
        else if(n == n.parent.left)
            n.parent.left = m;
        else if(n == n.parent.mid)
            n.parent.mid = m;
        else
            n.parent.right = m;
        n.parent = m;
        return m;
    }
    
    private void insertIntoLists(Node suggestion) {
        for(Node n = suggestion, m = n.mid; n != null; m = n, n = n.parent) {
            if(n.mid == m && m != null) {
                Node[] list = n.list;
                if(list.length < k) {
                    Node[] a = new Node[list.length + 1];
                    int i = list.length;
                    while(i > 0 && suggestion.weight > list[i - 1].weight) {
                        a[i] = list[i - 1];
                        i--;
                    }
                    a[i] = suggestion;
                    System.arraycopy(list, 0, a, 0, i);
                    n.list = a;
                }else if(suggestion.weight > list[k - 1].weight) {
                    int i = k - 1;
                    while(i > 0 && suggestion.weight > list[i - 1].weight) {
                        list[i] = list[i - 1];
                        i--;
                    }
                    list[i] = suggestion;
                }else
                    return;
            }
        }
    }
    
    private void updateListsIncreasedWeight(Node suggestion) {
        int i = 0;
        for(Node n = suggestion, m = n.mid; n != null; m = n, n = n.parent) {
            if(n.mid == m && m != null) {
                Node[] list = n.list;
                while(i < k && suggestion != list[i])
                    i++;
                if(i == k && suggestion.weight <= list[i - 1].weight)
                    return;
                int j = (i < k) ? i : i - 1;
                while(j > 0 && suggestion.weight > list[j - 1].weight) {
                    list[j] = list[j - 1];
                    j--;
                }
                list[j] = suggestion;
            }
        }
    }
    
    private void updateListsDecreasedWeight(Node suggestion) {
        int i = 0;
        for(Node n = suggestion, m = n.mid; n != null; m = n, n = n.parent) {
            if(n.mid == m && m != null) {
                Node[] list = n.list;
                while(i < k && suggestion != list[i])
                    i++;
                if(i == k)
                    return;
                int j = i;
                while(j < list.length - 1
                        && suggestion.weight < list[j + 1].weight) {
                    list[j] = list[j + 1];
                    j++;
                }
                if(j < k - 1)
                    list[j] = suggestion;
                else{
                    Node bus = bestUnlistedSuggestion(n);
                    if(bus != null && bus.weight > suggestion.weight)
                        list[j] = bus;
                    else
                        list[j] = suggestion;
                }
            }
        }
    }
    
    private Node bestUnlistedSuggestion(Node n) {
        Node[] list = n.list;
        Node result = null;
        if(n.weight != -1) {
            int i = 0;
            while(i < k && n != list[i])
                i++;
            if(i == k)
                result = n;
        }
        for(Node c = firstChild(n); c != null; c = nextChild(c)) {
            secondForLoop:
            for(int i = 0, j = 0; i < c.list.length; i++, j++) {
                Node suggestion = c.list[i];
                for(; j < k; j++) {
                    if(suggestion == list[j])
                        continue secondForLoop;
                }
                if(result == null || result.weight < suggestion.weight)
                    result = suggestion;
                break secondForLoop;
            }
        }
        return result;
    }
    
    private Node firstChild(Node n) {
        n = n.mid;
        if(n != null) {
            while(n.left != null)
                n = n.left;
        }
        return n;
    }
    
    private Node nextChild(Node child) {
        if(child.right != null) {
            Node n = child.right;
            while(n.left != null)
                n = n.left;
            return n;
        }else{
            Node n = child.parent;
            Node m = child;
            while(m == n.right) {
                m = n;
                n = n.parent;
            }
            return (m == n.left) ? n : null;
        }
    }
    
    /**
     * Removes the specified suggestion from this tree, if present.
     * @throws NullPointerException if the specified suggestion is {@code null}
     */
    public void remove(String suggestion) {
        Node n = getNode(suggestion);
        if(n == null)
            return;
        n.weight = -1;
        size--;
        Node m = n;
        if(n.mid == null) {
            Node replacement = removeNode(n);
            if(replacement != null)
                replacement.parent = n.parent;
            if(n == root)
                root = replacement;
            else if(n == n.parent.mid)
                n.parent.mid = replacement;
            else{
                if(n == n.parent.left)
                    n.parent.left = replacement;
                else
                    n.parent.right = replacement;
                while(n != root && n != n.parent.mid)
                    n = n.parent;
            }
            n = n.parent;
            if(n == null)
                return;
        }
        if(n.weight == -1 && n.mid.left == null && n.mid.right == null) {
            n = mergeWithChild(n);
            while(n != root && n != n.parent.mid)
                n = n.parent;
            n = n.parent;
            if(n == null)
                return;
        }
        removeFromLists(m, n);
    }
        
    private Node removeNode(Node n) {
        Node replacement;
        if(n.left == null)
            replacement = n.right;
        else if(n.right == null)
            replacement = n.left;
        else if(random.nextBoolean() == true) {
            replacement = n.right;
            if(replacement.left != null) {
                while(replacement.left != null)
                    replacement = replacement.left;
                replacement.parent.left = replacement.right;
                if(replacement.right != null)
                    replacement.right.parent = replacement.parent;
                replacement.right = n.right;
                n.right.parent = replacement;
            }
            replacement.left = n.left;
            n.left.parent = replacement;
        }else{
            replacement = n.left;
            if(replacement.right != null) {
                while(replacement.right != null)
                    replacement = replacement.right;
                replacement.parent.right = replacement.left;
                if(replacement.left != null)
                    replacement.left.parent = replacement.parent;
                replacement.left = n.left;
                n.left.parent = replacement;
            }
            replacement.right = n.right;
            n.right.parent = replacement;
        }
        return replacement;
    }
    
    private Node mergeWithChild(Node n) {
        Node child = n.mid;
        child.firstChar = n.firstChar;
        child.left = n.left;
        if(child.left != null)
            child.left.parent = child;
        child.right = n.right;
        if(child.right != null)
            child.right.parent = child;
        child.parent = n.parent;
        if(n == root)
            root = child;
        else if(n == n.parent.left)
            n.parent.left = child;
        else if(n == n.parent.mid)
            n.parent.mid = child;
        else
            n.parent.right = child;
        return child;
    }
    
    private void removeFromLists(Node suggestion, Node firstList) {
        int i = 0;
        for(Node n = firstList, m = n.mid; n != null; m = n, n = n.parent) {
            if(n.mid == m) {
                if(n.weight == -1)
                    n.suggestion = n.mid.suggestion;
                Node[] list = n.list;
                while(i < k && suggestion != list[i])
                    i++;
                if(i < k) {
                    Node bus;
                    if(list.length == k
                            && (bus = bestUnlistedSuggestion(n)) != null) {
                        for(int j = i; j < k - 1; j++)
                            list[j] = list[j + 1];
                        list[k - 1] = bus;
                    }else{
                        int len = list.length;
                        Node[] a = new Node[len - 1];
                        System.arraycopy(list, 0, a, 0, i);
                        System.arraycopy(list, i + 1, a, i, len - i - 1);
                        n.list = a;
                    }
                }
            }
        }
    }
    
    /**
     * Returns an iterator over the suggestions in this tree.
     */
    public Iterator iterator() {
        return new Iterator();
    }
    
    /**
     * An iterator over the suggestions in the tree. The iterator returns the
     * suggestions in lexicographic order.
     */
    public final class Iterator {
        
        private Node current;
        private boolean initialState;
        
        private Iterator() {
            current = null;
            initialState = true;
        }
        
        /**
         * Returns the next suggestion in the iteration, or {@code null} if the
         * iteration has no more suggestions.
         * @throws IllegalStateException if the last call to {@code next}
         * returned {@code null}
         * @throws ConcurrentModificationException if the last suggestion
         * returned has been removed from the tree
         */
        public String next() {
            if(current == null) {
                if(!initialState)
                    throw new IllegalStateException();
                if(root != null)
                    current = firstSuggestion(root);
                initialState = false;
            }else if(current.weight == -1)
                throw new ConcurrentModificationException();
            else
                current = nextSuggestion();
            return (current != null) ? current.suggestion : null;
        }
        
        private Node firstSuggestion(Node n) {
            while(true) {
                while(n.left != null)
                    n = n.left;
                if(n.weight == -1)
                    n = n.mid;
                else
                    return n;
            }
        }

        private Node nextSuggestion() {
            if(current.mid != null)
                return firstSuggestion(current.mid);
            else if(current.right != null)
                return firstSuggestion(current.right);
            else if(current.parent == null)
                return null;
            Node n = current.parent;
            Node m = current;
            while(m == n.right || m == n.mid && n.right == null) {
                m = n;
                n = n.parent;
                if(n == null)
                    return null;
            }
            if(m == n.left)
                return (n.weight != -1) ? n : firstSuggestion(n.mid);
            else
                return firstSuggestion(n.right);
        }

        /**
         * Returns the weight of the last suggestion returned.
         * @throws IllegalStateException if the {@code next} method has not yet
         * been called or the last call to {@code next} returned {@code null}
         * @throws ConcurrentModificationException if the last suggestion
         * returned has been removed from the tree
         */
        public int weight() {
            if(current == null)
                throw new IllegalStateException();
            if(current.weight == -1)
                throw new ConcurrentModificationException();
            return current.weight;
        }
    }
    
    /**
     * A tree node with a rank-ordered list of autocomplete suggestions. The
     * highest weighted suggestion is at index 0, the second highest weighted at
     * index 1, and so on.
     */
    public static final class Node {
        
        private Node[] list;
        private String suggestion;
        private String key;
        private int weight;
        private char firstChar;
        private final short charEnd;
        private Node left, mid, right, parent;
        
        private Node(String suggestion, int weight,String key, int index, Node parent) {
            list = new Node[] {this};
            this.suggestion = suggestion;
            this.weight = weight;
            this.key = key;
            firstChar = suggestion.charAt(index);
            charEnd = (short) suggestion.length();
            left = mid = right = null;
            this.parent = parent;
        }
        
        private Node(Node[] list, Node n, int charEnd) {
            this.list = list;
            suggestion = n.suggestion;
            weight = -1;
            firstChar = n.firstChar;
            this.charEnd = (short) charEnd;
            left = n.left;
            mid = n;
            right = n.right;
            parent = n.parent;
        }
        
        /**
         * Returns the suggestion at the specified position in the list.
         * @throws IndexOutOfBoundsException if the {@code index} argument is
         * negative or not less than the list length
         */
        public String getSuggestion(int index) {
            return list[index].suggestion;
        }
        
        /**
         * Returns the weight of the suggestion at the specified position in the
         * list.
         * @throws IndexOutOfBoundsException if the {@code index} argument is
         * negative or not less than the list length
         */
        public int getWeight(int index) {
            return list[index].weight;
        }
        
        /**
         * Returns the key of the suggestion at the specified position in the list
         * @throws IndexOutOfBoundsException if the {@code index} argument is
         * negative or not less than the list length
         * @author Rene Pickhardt
         */
        public String getKey(int index){
        	return list[index].key;
        }
        
        /**
         * Returns the number of suggestions in the list.
         */
        public int listLength() {
            return list.length;
        }
    }
}
