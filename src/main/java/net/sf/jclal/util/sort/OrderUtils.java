/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.jclal.util.sort;

import java.util.Collections;
import java.util.List;

/**
 * Utility class that implements a collection of ordering algorithms.
 *
 * @author Oscar Gabriel Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class OrderUtils {

    /**
     * @param doubles The array
     * @param begin Index where start the algorithm, inclusive.
     * @param end Index where end the algorithm, exclusive.
     * @return The max index
     */
    public static int maxIndex(double[] doubles, int begin, int end) {
        if (doubles == null || doubles.length == 0) {
            return -1;
        }
        double max = doubles[begin];
        int pos = begin;
        for (int i = begin + 1; i < end; i++) {
            if (max < doubles[i]) {
                max = doubles[i];
                pos = i;
            }
        }
        return pos;
    }

    /**
     * MergeSort algorithm. O(NlogN)
     *
     * This implementation uses internally the merge sort implementation of the
     * JVM
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant 
     * order, false otherwise
     */
    public static void mergeSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        Collections.sort(array, comparator);
    }

    /**
     * QuickSort algorithm. O(NlogN).
     *
     * This is an implementation of the classic Hoare version of the quick sort
     * algorithm
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void quickSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        quickSort(array, 0, array.size() - 1, comparator);
    }

    private static void quickSort(List<Container> array, int first, int last, SortComparator comparator) {

        if (first < last) {
            int l = pivot(array, array.get(first), first, last, comparator);
            quickSort(array, first, l - 1, comparator);
            quickSort(array, l + 1, last, comparator);
        }
    }

    private static int pivot(List<Container> array, Container p, int first, int last, SortComparator comparator) {

        int i = first;
        int l = last + 1;

        do {
            ++i;
        } while (i < last && comparator.compare(array.get(i), p) <= 0);

        do {
            --l;
        } while (comparator.compare(array.get(l), p) == 1);

        while (i < l) {

            //swap the elements
            swap(array, i, l);

            do {
                ++i;
            } while (comparator.compare(array.get(i), p) <= 0);

            do {
                --l;
            } while (comparator.compare(array.get(l), p) == 1);

        }

        //swap the elements
        swap(array, first, l);

        return l;

    }

    /**
     * Swap the element into the array
     *
     * @param array An array
     * @param i The i-th element to swap
     * @param j The j-th element to swap
     */
    public static void swap(List<Container> array, int i, int j) {

        Container temp = array.get(i).copy();

        array.set(i, array.get(j));

        array.set(j, temp);
    }

    /**
     * BurbleSort algorithm. O(N^2)
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void burbleSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        int i, j, size = array.size();

        for (i = 0; i < size - 1; ++i) {

            for (j = size - 1; j > i; --j) {

                if (comparator.compare(array.get(j - 1), array.get(j)) == 1) {
                    swap(array, j, j - 1);
                }
            }
        }
    }

    /**
     * InsertionSort algorithm. O(N^2)
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void insertionSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        int size = array.size();
        int j;
        Container x;
        int i;

        for (i = 1; i < size; ++i) {
            x = array.get(i).copy();
            j = i - 1;

            while (j >= 0 && comparator.compare(x, array.get(j)) == -1) {
                array.set(j + 1, array.get(j));
                --j;
            }
            array.set(j + 1, x);

        }
    }

    /**
     * SeletionSort algorithm. O(N^2)
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void seletionSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        int size = array.size();

        int i, j, smaller;

        for (i = 0; i < size - 1; ++i) {
            for (j = i + 1, smaller = i; j < size; ++j) {
                if (comparator.compare(array.get(j), array.get(smaller)) == -1) {
                    smaller = j;
                }
            }
            swap(array, i, smaller);
        }
    }

    /**
     * ShellSort algorithm. O(N^2)
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void shellSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        int size = array.size();

        int jump, changes, i;

        for (jump = size / 2; jump != 0; jump /= 2) {
            for (changes = 1; changes != 0;) {
                changes = 0;
                for (i = jump; i < size; ++i) {
                    if (comparator.compare(array.get(i - jump), array.get(i)) == 1) {

                        swap(array, i, i - jump);
                        ++changes;
                    }
                }
            }
        }
    }

    /**
     * HeapSort algorithm. O(NlogN)
     *
     * @param array The array to order
     * @param descendentOrder True if the array will be ordered in descendant
     * order, false otherwise
     */
    public static void heapSort(List<Container> array, boolean descendentOrder) {

        SortComparator comparator = new SortComparator(descendentOrder);

        heapSort(array, 0, array.size() - 1, comparator);
    }

    private static void heapSort(List<Container> array, int first, int last, SortComparator comparator) {

        doHeap(array, first, last, comparator);

        for (int i = last; i >= first + 1; --i) {

            swap(array, first, i);
            Push(array, first, i - 1, first, comparator);
        }
    }

    private static void doHeap(List<Container> array, int first, int last, SortComparator comparator) {

        for (int i = (last - first + 1) / 2; i >= 1; --i) {
            Push(array, first, last, first + i - 1, comparator);
        }

    }

    private static void Push(List<Container> array, int first, int last, int i, SortComparator comparator) {

        int k = i - first + 1;
        int j;

        do {

            j = k;
            if (2 * j <= last - first + 1 && comparator.compare(array.get(2 * j + first - 1), array.get(k + first - 1)) == 1) {
                k = 2 * j;
            }

            if (2 * j < last - first + 1 && comparator.compare(array.get(2 * j + first), array.get(k + first - 1)) == 1) {
                k = 2 * j + 1;
            }

            int index1 = j + first - 1;
            int index2 = k + first - 1;

            swap(array, index1, index2);

        } while (j != k);
    }

}
