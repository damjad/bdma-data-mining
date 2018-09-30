package com.danish.dm.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node<T>
{
    private Map<String, Node<T>> children = new ConcurrentHashMap<>();
    private Node<T> parent = null;
    private String pathToParent;

    private T data;

    public Node(T data)
    {
        this.data = data;
    }

    public Node(Node<T> parent, T data)
    {
        this.parent = parent;
        this.data = data;
    }

    public Map<String, Node<T>> getChildren()
    {
        return children;
    }

    public void addChild(String key, Node<T> child)
    {
        children.put(key, child);
        child.setParent(this);
        child.setPathToParent(key);
    }

    public Node<T> getParent()
    {
        return parent;
    }

    public void setParent(Node<T> parent)
    {
        this.parent = parent;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public String getPathToParent()
    {
        return pathToParent;
    }

    public void setPathToParent(String pathToParent)
    {
        this.pathToParent = pathToParent;
    }

    public void display()
    {
        List<Node<T>> leafNodes = new ArrayList<>();
        getLeafNodes(leafNodes);

        //TODO: remove this
        System.out.println("Number of leaves: " + leafNodes.size());

        for (Node<T> leaf : leafNodes)
        {
            leaf.printUpperHierarchy();
        }
    }

    public void printUpperHierarchy()
    {
        if (children.isEmpty() ||  parent == null)
            System.out.print(" " + this.data + "<>");

        if (parent != null)
        {
            System.out.print(" " + this.parent.getData() + "=" +this.pathToParent + " -->");
            parent.printUpperHierarchy();
        }
        else
        {
            System.out.println();
        }
    }

    public void getLeafNodes(List<Node<T>> leafNodes)
    {

        for (Map.Entry<String, Node<T>> child: children.entrySet())
        {
            child.getValue().getLeafNodes(leafNodes);
        }

        if(children.isEmpty())
        {
            leafNodes.add(this);
        }
    }
}
