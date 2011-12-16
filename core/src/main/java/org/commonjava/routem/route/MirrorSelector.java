package org.commonjava.routem.route;

import java.util.List;

import org.commonjava.routem.model.MirrorOf;

public interface MirrorSelector
{

    MirrorOf select( List<MirrorOf> mirrors );

}
