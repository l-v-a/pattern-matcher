package lva.classfinder;

import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author vlitvinenko
 */
@ToString
class ClassName implements CharSequence, Comparable<ClassName> {
    @Getter
    private final String simpleName;
    @Getter
    private final String packageName;

    ClassName(@NonNull ClassPath.ClassInfo classInfo) {
        this.simpleName = classInfo.getSimpleName();
        this.packageName = classInfo.getPackageName();
    }

    @Override
    public int length() {
        return simpleName.length();
    }

    @Override
    public char charAt(int index) {
        return simpleName.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return simpleName.subSequence(start, end);
    }

    @Override
    public int compareTo(ClassName className) {
        int res = simpleName.compareTo(className.simpleName);
        if (res == 0) {
            res = packageName.compareTo(className.packageName);
        }
        return res;
    }
}
