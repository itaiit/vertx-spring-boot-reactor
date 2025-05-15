package io.itaiit.vertx;

import io.vertx.core.MultiMap;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

class VertxHeadersAdapter implements MultiValueMap<String, String> {

    private final MultiMap headers;

    public VertxHeadersAdapter(MultiMap headers) {
        this.headers = headers;
    }

    @Override
    public String getFirst(String key) {
        return headers.get(key);
    }

    @Override
    public void add(String key, String value) {
        headers.add(key, value);
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        headers.add(key, (List<String>) values);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        for (Map.Entry<String, List<String>> entry : values.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void set(String key, String value) {
        headers.set(key, value);
    }

    @Override
    public void setAll(Map<String, String> values) {
        clear();
        headers.setAll(values);
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        Map<String, String> result = new HashMap<>();
        for (Entry<String, String> entry : headers.entries()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public int size() {
        return headers.names().size();
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.headers.contains((String) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return (value instanceof String &&
                this.headers.entries().stream()
                        .anyMatch(entry -> value.equals(entry.getValue())));
    }

    @Override
    public List<String> get(Object key) {
        if (containsKey(key)) {
            return this.headers.getAll((String) key);
        }
        return null;
    }

    @Override
    public List<String> put(String key, List<String> value) {
        List<String> previousValues = this.headers.getAll(key);
        this.headers.set(key, value);
        return previousValues;
    }

    @Override
    public List<String> remove(Object key) {
        if (key instanceof String) {
            List<String> previousValues = this.headers.getAll((String) key);
            this.headers.remove((String) key);
            return previousValues;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        m.forEach(this.headers::set);
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return new HeaderNames();
    }

    @Override
    public Collection<List<String>> values() {
        return this.headers.names().stream()
                .map(this.headers::getAll).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return new AbstractSet<Entry<String, List<String>>>() {
            @Override
            public Iterator<Entry<String, List<String>>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return headers.size();
            }
        };
    }


    private class EntryIterator implements Iterator<Entry<String, List<String>>> {

        private Iterator<String> names = headers.names().iterator();

        @Override
        public boolean hasNext() {
            return this.names.hasNext();
        }

        @Override
        public Entry<String, List<String>> next() {
            return new VertxHeadersAdapter.HeaderEntry(this.names.next());
        }
    }


    private class HeaderEntry implements Entry<String, List<String>> {

        private final String key;

        HeaderEntry(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public List<String> getValue() {
            return headers.getAll(this.key);
        }

        @Override
        public List<String> setValue(List<String> value) {
            List<String> previousValues = headers.getAll(this.key);
            headers.set(this.key, value);
            return previousValues;
        }
    }

    private class HeaderNames extends AbstractSet<String> {

        @Override
        public Iterator<String> iterator() {
            return new VertxHeadersAdapter.HeaderNamesIterator(headers.names().iterator());
        }

        @Override
        public int size() {
            return headers.names().size();
        }
    }

    private final class HeaderNamesIterator implements Iterator<String> {

        private final Iterator<String> iterator;

        @Nullable
        private String currentName;

        private HeaderNamesIterator(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public String next() {
            this.currentName = this.iterator.next();
            return this.currentName;
        }

        @Override
        public void remove() {
            if (this.currentName == null) {
                throw new IllegalStateException("No current Header in iterator");
            }
            if (!headers.contains(this.currentName)) {
                throw new IllegalStateException("Header not present: " + this.currentName);
            }
            headers.remove(this.currentName);
        }
    }
}
