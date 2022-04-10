package loan;

import exception.AbsException;
import jaxb.generated.AbsCategories;
import utils.ABSUtils;

import java.util.HashSet;
import java.util.Set;

public class Categories {
    private Set<String> allCategories;

    public Categories(Set<String> allCategories) {
        this.allCategories = allCategories;
    }

    public static Categories ConvertRawAbsToCategories(AbsCategories rawVer) throws AbsException {
        Set<String> resSet = new HashSet<>();
        for (String category : rawVer.getAbsCategory()
        ) {
            resSet.add(ABSUtils.sanitizeStr(category));
        }
        Categories res = new Categories(resSet);
        return res;
    }

    public Set<String> getAllCategories() {
        return allCategories;
    }
}
