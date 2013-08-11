package all;

import java.util.Arrays;

class ArrayListLong {
    private int size;
    private long[] array;

    public ArrayListLong () {
        size = 0;
        array = new long[16];
    }

    public void add (long l) {
        if (size == array.length) {
            array = Arrays.copyOf(array, size * 2);
        }
        array[size] = l;
        size++;
    }

    public void addAll (long[] ls) {
        array = Arrays.copyOf(array, array.length + ls.length);
        System.arraycopy(ls, 0, array, size, ls.length);
        size += ls.length;
    }

    public long[] toArray () {
        return Arrays.copyOf(array, size);
    }
}