package com.madxstudio.co8.ui.workspace.dummy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Workspace> ITEMS = new ArrayList<Workspace>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Workspace> ITEM_MAP = new HashMap<String, Workspace>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Workspace item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Workspace createDummyItem(int position) {
        Workspace result = new Workspace(String.valueOf(position), "Item " + position, makeDetails(position));
        result.setDate(new Date().toString());
        result.setName("Jackie");
        return result;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Workspace {
        private String id;
        private String title;
        private String details;
        private String name;
        private String date;

        public Workspace(String id, String title, String details) {
            this.id = id;
            this.title = title;
            this.details = details;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
