package ca.uoit.projectxiphos;
import java.io.*;
import java.io.Serializable;

class CFile implements Serializable
{
    public String name;
    public long size;
    public String owner;
    public byte[] data;

    public CFile(String n, int s, String o)
    {
        name = n;
        size = s;
        owner = o;
    }

    public CFile()
    {
        name = "Filler file";
        size = 1;
        owner = "Public";
    }

    @Override
    public String toString() {
        return "CFile [name=" + name + ", size=" +  String.valueOf(size) + ", owner=" + owner  + " , data=" + data.toString() + "]";
    }
}