package linda;

import java.io.Serializable;

public class AnyTuple extends Tuple{
    public AnyTuple() {
        super();
    }

    @Override
    public boolean matches(Tuple template) {
        return true;
    }


}
