package sp.security;

import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import sp.model.Register;

/**
 * Reports! {@link PermissionEvaluator} implementation
 *
 * NOTE: now implemented yet
 * 
 * @author Paul Kulitski
 * @see PermissionEvaluator
 */
public class ReportsPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication a, Object target, Object permission) {
        if (target instanceof Register) {
            if (a.getName().equals(((Register) target).getUser().getFullname())) {
                return true;
            } else if (permission.equals("read")) {
                return true;
            } else {
                return false;
            }
        }
        throw new UnsupportedOperationException(
                "hasPermission not supported for object<" + target
                + "> and permission <" + permission + ">");
    }

    @Override
    public boolean hasPermission(Authentication a, Serializable targetId,
            String type, Object permission) {
        throw new UnsupportedOperationException(
                "hasPermission not supported for object<" + targetId
                + "> and permission <" + permission + ">");
    } 
}
