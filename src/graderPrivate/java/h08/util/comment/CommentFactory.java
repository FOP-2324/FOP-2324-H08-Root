package h08.util.comment;

import java.util.List;

public abstract class CommentFactory<T> {

    public abstract String build(T object);

    public String build(T[] objects) {
        return build(objects, null);
    }

    public String build(T[] objects, String separator) {
        StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for (T object : objects) {
            if (!first) {
                builder.append(", ");
                if (separator != null) {
                    builder.append(separator);
                }
            }
            if (object == null) {
                builder.append("null");
            } else {
                builder.append(build(object));
            }
            first = false;
        }
        builder.append("]");
        return builder.toString();
    }

    public String build(List<T> objects) {
        return build(objects, null);
    }

    @SuppressWarnings("unchecked")
    public String build(List<T> objects, String separator) {
        return build(objects.toArray((T[]) new Object[objects.size()]), separator);
    }
}
