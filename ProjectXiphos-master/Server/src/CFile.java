import java.io.*;
import java.io.Serializable;

class CFile implements Serializable
{
    public String name;
    public long size;
    public String group;
    public String owner;
    public String path;
    public boolean isPrivate;

    public CFile(String n, int s, String g, String o, String p, boolean b)
    {
        name = n;
        size = s;
        group = g;
        owner  = o;
        path = p;
        isPrivate = b;
    }

    public CFile()
    {
        name = "Filler file";
        size = 1;
        group = "Public";
        owner = "This username should not exist.";
        path = "/Files/Public";
        isPrivate = false;
    }

    @Override
    public String toString() {
        return "CFile [name=" + name + ", size=" +  String.valueOf(size) + ", group=" + group + ", owner=" + owner + ", path=" + path + ", isPrivate=" + String.valueOf(isPrivate) + "]";
    }
}