package com.pdt.blissrecruitment.entities;

import com.pdt.blissrecruitment.Util.Constants;
import com.pdt.blissrecruitment.ui.list.ListFragment;

/**
 * Created by pdt on 29/07/2017.
 */

public class ListInfo {
    private int offset;
    private int limit;
    private @ListFragment.ListMode int listMode;
    private String filter;

    private ListInfo(ListInfoBuilder listInfoBuilder) {
        this.offset = listInfoBuilder.offset;
        this.limit = listInfoBuilder.limit;
        this.listMode = listInfoBuilder.listMode;
        this.filter = listInfoBuilder.filter;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "ListInfo{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", listMode=" + listMode +
                ", filter='" + filter + '\'' +
                '}';
    }

    //
    public boolean isDifferentSearch(String newFilter) {
        return filter.equals(newFilter);
//        return listMode == ListFragment.ListMode.FILTERED &&
//                !TextUtils.isEmpty(filter) &&
//                !TextUtils.isEmpty(newFilter) &&
//                filter.equals(newFilter);
    }

    public static class ListInfoBuilder {

        private @ListFragment.ListMode int listMode;
        private int offset;
        private int limit;

        private String filter;

        public ListInfoBuilder(@ListFragment.ListMode int listMode) {
            this.listMode = listMode;
            this.limit = Constants.LIMIT;
            this.offset = offset;

        }
//
//        public ListInfoBuilder offset(int offset) {
//            this.offset = offset;
//            return this;
//        }

        public ListInfoBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }



        public ListInfo build() {
            return new ListInfo(this);
        }
    }
}
