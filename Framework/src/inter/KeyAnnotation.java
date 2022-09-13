package inter;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface KeyAnnotation{
    public String column() default "";
    public String nameTable() default "";
}
