/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.util;

import java.util.LinkedList;

/**
 *
 * @author denijal
 */
class Node<E> {

    private E data;
    private int count;
    private Node<E> left;
    private Node<E> right;

    public Node(E d) {
        this.data = d;
        this.count = 1;
        this.right = null;
        this.left = null;
    }

    public E getData() {
        return data;
    }

    public Node<E> getRight() {
        return right;
    }

    public int getCount() {
        return count;
    }

    public Node<E> getLeft() {
        return left;
    }

    public void setData(E data) {
        this.data = data;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setLeft(Node<E> left) {
        this.left = left;
    }

    public void setRight(Node<E> right) {
        this.right = right;
    }

    public void incrementCount() {
        count++;
    }

    public void decreaseCount() {
        count--;
    }

    public boolean hasLeft() {
        return (left != null);
    }

    public boolean hasRight() {
        return (right != null);
    }
}

public class Bag<E extends Comparable<E>> {

    private Node<E> root;

    public Bag() {

        root = null;
    }

    public Bag(Node newNode) {
        root = newNode;
    }

    public void add(E data) {

        System.out.println("Who wants me to add: " + data);
        if (isEmpty()) {
            Node<E> newNode = new Node<E>(data);
            root = newNode;

        } else {
            Node<E> current = root;
            while (true) {
                if (data.compareTo(current.getData()) < 0) {
                    if (current.getLeft() != null) {
                        current = current.getLeft();
                    } else {
                        Node<E> newNode = new Node<E>(data);
                        current.setLeft(newNode);
                        break;
                    }
                } else if (data.compareTo(current.getData()) > 0) {
                    if (current.getRight() != null) {
                        current = current.getRight();
                    } else {
                        Node<E> newNode = new Node<E>(data);
                        current.setRight(newNode);
                        break;
                    }
                } else if (data.compareTo(current.getData()) == 0) {

                    current.incrementCount();
                    break;
                }
            }
        }
    }

    public boolean remove(E data) {
        if (!contains(data)) {
            System.out.println("The item '" + data + "' is not located in the Bag");
            return false;
        } else {
            Node<E> temp = remove(root, data);
            if (temp != null && temp.getData() != null && contains(temp.getData())) {
                return true;
            } else {
                return false;
            }
        }
    }

    private Node<E> remove(Node<E> root, E data) {

        Node<E> current = root;
        if (current == null) {
            return current;
        }
        if (data.compareTo(current.getData()) < 0) {
            current.setLeft(remove(current.getLeft(), data));
        } else if (data.compareTo(current.getData()) > 0) {
            current.setRight(remove(current.getRight(), data));
        } else {
            if (current.getCount() > 1) {
                current.decreaseCount();
                return current;
            } else {
                if (current.getLeft() != null && current.getRight() != null) {
                    Node<E> MinFromRightSubTree = findMinFromRight(current.getRight());
                    current.setData(MinFromRightSubTree.getData());
                    remove(current.getRight(), MinFromRightSubTree.getData());
                } else if (current.getLeft() != null) {
                    current = current.getLeft();
                } else if (current.getRight() != null) {
                    current = current.getRight();
                } else {
                    System.out.println("Empty");
                    if (current == this.root) {
                        this.root = null;
                    }

                    current = null;
                }
            }
        }
        return current;
    }

    private Node<E> findMinFromRight(Node<E> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    public boolean contains(E data) {
        Node<E> current = root;
        boolean found = true;
        while (found) {
            if (data.compareTo(current.getData()) < 0) {
                if (current.getLeft() != null) {
                    current = current.getLeft();
                } else {
                    break;
                }

            }
            if (data.compareTo(current.getData()) > 0) {
                if (current.getRight() != null) {
                    current = current.getRight();
                } else {
                    break;
                }

            }
            if (data.compareTo(current.getData()) == 0) {

                found = false;
            }
        }
        return !found;
    }

    public boolean isEmpty() {
        if (root == null) {
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        root = null;
    }

    public int distictSize() {
        return (distictSize(root));
    }

    private int distictSize(Node<E> node) {
        if (node == null) {
            return (0);
        } else {
            return (distictSize(node.getLeft()) + 1 + distictSize(node.getRight()));
        }
    }

    public int elementSize(E data) {
        Node<E> current = root;

        int elementsize = 0;
        if (!contains(data)) {
            System.out.print(data + " is not found in the Bag, " + data + ":");
            return 0;
        } else {
            while (true) {
                if (data.compareTo(current.getData()) < 0) {
                    if (current.getLeft() != null) {
                        current = current.getLeft();
                    }
                } else if (data.compareTo(current.getData()) > 0) {
                    if (current.getRight() != null) {
                        current = current.getRight();
                    }
                } else if (data.compareTo(current.getData()) == 0) {
                    elementsize = current.getCount();
                    break;
                }
            }
            return elementsize;
        }
    }

    public int size() {
        return size(root);
    }

    private int size(Node<E> node) {
        int size = 0;
        if (node == null) {
            return (0);
        } else {
            return node.getCount() + size(node.getLeft()) + size(node.getRight());
        }
    }

    public LinkedList<E> inOrder() {
        LinkedList<E> result = new LinkedList<>();
        if (root != null) {
            addToList(root, result);
        }
        return result;
    }

    private void addToList(Node<E> current, LinkedList<E> order) {
        if (current.hasLeft()) {
            addToList(current.getLeft(), order);
        }
        for (int i = 0; i < current.getCount(); i++) {
            if (i == 0) {
                order.add(current.getData());
            } else {
                if (current.getData() instanceof CardLabel) {
                    order.add((E) new CardLabel((CardLabel) current.getData()));
                }
            }
        }
        if (current.hasRight()) {
            addToList(current.getRight(), order);
        }

    }

    public static void printInorder(Bag bag) {
        if (bag.root == null) {
            return;
        } else {
            if (bag.root.hasLeft()) {
                printInorder(new Bag(bag.root.getLeft()));
            }

            System.out.print(bag.root.getData() + " : " + bag.root.getCount() + "  ");

            if (bag.root.hasRight()) {
                printInorder(new Bag(bag.root.getRight()));
            }
        }

    }

    public String toString() {
        return toString(root);
    }

    private String toString(Node<E> root) {
        if (isEmpty()) {
            return "Bag is empty";
        } else {
            Node<E> current = root;
            String result = "";
            if (current == null) {
                return "";
            }
            result += toString(current.getLeft());
            result += "{" + current.getData().toString() + " : " + current.getCount() + "}";
            result += toString(current.getRight());

            return result;

        }

    }

}
