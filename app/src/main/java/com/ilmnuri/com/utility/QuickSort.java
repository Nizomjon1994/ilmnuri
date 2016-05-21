package com.ilmnuri.com.utility;

import com.ilmnuri.com.model.AlbumModel;

import java.util.ArrayList;


public class QuickSort {

    ArrayList<AlbumModel> albumModels;
    public QuickSort (ArrayList<AlbumModel> albumModels) {
        this.albumModels = albumModels;
    }
    public ArrayList<AlbumModel> getResult() {
        return this.albumModels;
    }

 
    public void sort() {
        if (albumModels == null || albumModels.size() <= 0) {
            return;
        }
 
//        System.out.println("Input Array: " + Arrays.toString(items));
        quickSort(0, albumModels.size() - 1);
//        System.out.println("Sorted Array: " + Arrays.toString(items));
 
    }
     
    /**
     * Recursively apply quickSort - one for partition smaller than pivot -
     * another for partition bigger than pivot
     */
    public void quickSort( int start, int end) {
        if (start >= end) {
            return;
        }
 
        int pivot = partition(start, end);
 
        if (start < pivot - 1) {
            quickSort( start, pivot - 1);
        }
 
        if (end > pivot) {
 
            quickSort( pivot, end);
        }
 
         
    }
 
    /**
     * Reorganizes the given list so all elements less than the first are before
     * it and all greater elements are after it.
     */
    private  int partition( int start, int end) {
 
        int pivot = albumModels.get((start + end) / 2).getId();
        while (start <= end) {
            while (albumModels.get(start).getId() < pivot) {
                start++;
            }
            while (albumModels.get(start).getId() > pivot) {
                end--;
            }
 
            if (start <= end) {
                swap( start, end);
                start++;
                end--;
            }
        }
        return start;
 
    }
 
 
 
    /**
     * Swaps data from an array.
     */
    private void swap( int firstIndex, int secondIndex) {
        if (albumModels == null || firstIndex < 0 || firstIndex > albumModels.size()
                || secondIndex < 0 || secondIndex > albumModels.size()) {
            return;
        }
        AlbumModel albumModel = albumModels.get(firstIndex);
        albumModels.add(firstIndex, albumModels.get(secondIndex));
        albumModels.remove(firstIndex + 1);
        albumModels.add(secondIndex, albumModel);
        albumModels.remove(secondIndex + 1);

    }
 
}