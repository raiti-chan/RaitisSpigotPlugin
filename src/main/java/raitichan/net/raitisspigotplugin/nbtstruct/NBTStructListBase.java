package raitichan.net.raitisspigotplugin.nbtstruct;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class NBTStructListBase<T extends NBTStructBase> implements List<T>, Iterable<T> {
    public final NBTCompoundList compoundList;

    protected NBTStructListBase(NBTCompoundList compoundList) {
        this.compoundList = compoundList;
    }

    protected abstract T createStructInstance(NBTCompound compound);

    public T addNBTStruct() {
        return this.createStructInstance(this.compoundList.addCompound());
    }

    @Override
    public int size() {
        return this.compoundList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.compoundList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof NBTStructBase) {
            NBTStructBase nbtStructBase = (NBTStructBase) o;
            return this.compoundList.contains(nbtStructBase.compound);
        }
        return this.compoundList.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof NBTStructBase) {
            NBTStructBase nbtStructBase = (NBTStructBase) o;
            return this.compoundList.indexOf(nbtStructBase.compound);
        }
        return this.compoundList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof NBTStructBase) {
            NBTStructBase nbtStructBase = (NBTStructBase) o;
            return this.compoundList.lastIndexOf(nbtStructBase.compound);
        }
        return this.compoundList.lastIndexOf(o);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof NBTStructBase) {
            NBTStructBase nbtStructBase = (NBTStructBase) o;
            return this.compoundList.remove(nbtStructBase.compound);
        }
        return this.compoundList.remove(o);
    }

    @Override
    public T remove(int index) {
        ReadWriteNBT old = this.compoundList.remove(index);
        if (old instanceof NBTCompound) {
            return this.createStructInstance((NBTCompound) old);
        }
        return null;
    }

    @Override
    public void clear() {
        this.compoundList.clear();
    }

    @Override
    public boolean add(T t) {
        return this.compoundList.addCompound(t.compound) != null;
    }

    @Override
    public void add(int index, T element) {
        this.compoundList.add(index, element.compound);
    }

    @Override
    public T get(int index) {
        return this.createStructInstance(this.compoundList.get(index));
    }

    @Override
    public T set(int index, T element) {
        return this.createStructInstance(this.compoundList.set(index, element.compound));
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new NBTStructIterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        Object[] array = new Object[this.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.get(i);
        }
        return array;
    }

    @NotNull
    @Override
    public <E> E @NotNull [] toArray(@NotNull E @NotNull [] a) {
        E[] ar = this.size() < a.length ? a : Arrays.copyOf(a, this.size());
        Arrays.fill(ar, null);
        Class<?> arrayClass = a.getClass().getComponentType();
        for (int i = 0; i < this.size(); i++) {
            T obj = this.get(i);
            if (arrayClass.isInstance(obj)) {
                //noinspection unchecked
                ar[i] = (E) obj;
            } else {
                throw new ArrayStoreException("The array does not match the objects stored in the List.");
            }
        }
        return ar;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object ele : c) {
            if (!this.contains(ele)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        int size = this.size();
        for (T ele : c) {
            this.add(ele);
        }
        return size != size();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        int size = this.size();
        for (T ele : c) {
            this.add(index++, ele);
        }
        return size != this.size();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        int size = this.size();
        for (Object obj : c) {
            this.remove(obj);
        }
        return size != this.size();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        int size = this.size();
        for (Object obj : c) {
            for (int i = 0; i < this.size(); i++) {
                if (!obj.equals(this.get(i))) {
                    this.remove(i--);
                }
            }
        }
        return size != this.size();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new NBTStructListIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            list.add(this.get(i));
        }
        return list;
    }
    private class NBTStructIterator implements Iterator<T> {
        private int index = -1;

        @Override
        public boolean hasNext() {
            return NBTStructListBase.this.size() > index + 1;
        }

        @Override
        public T next() {
            if (!this.hasNext()) throw new NoSuchElementException();
            return NBTStructListBase.this.get(++index);
        }

        @Override
        public void remove() {
            NBTStructListBase.this.remove(index);
            index--;
        }
    }

    private class NBTStructListIterator implements ListIterator<T> {
        int index;

        private NBTStructListIterator(int startIndex) {
            this.index = startIndex - 1;
        }

        @Override
        public boolean hasNext() {
            return NBTStructListBase.this.size() > index + 1;
        }

        @Override
        public T next() {
            if (!this.hasNext()) throw new NoSuchElementException();
            return NBTStructListBase.this.get(++index);
        }

        @Override
        public boolean hasPrevious() {
            return this.index >= 0 && this.index <= NBTStructListBase.this.size();
        }

        @Override
        public T previous() {
            if (!this.hasPrevious()) throw new NoSuchElementException("ID: " + (index - 1));
            return NBTStructListBase.this.get(index--);
        }

        @Override
        public int nextIndex() {
            return this.index + 1;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            NBTStructListBase.this.remove(this.index);
            index--;
        }

        @Override
        public void set(T t) {
            NBTStructListBase.this.set(index, t);
        }

        @Override
        public void add(T t) {
            NBTStructListBase.this.add(index, t);
        }
    }
}
