package org.commonjava.routem.route;

import java.util.List;

import org.commonjava.routem.model.MirrorOf;

public class PickFirstMirrorSelector
    implements MirrorSelector
{

    @Override
    public MirrorOf select( final List<MirrorOf> mirrors )
    {
        return mirrors == null || mirrors.isEmpty() ? null : mirrors.get( 0 );
    }

}
