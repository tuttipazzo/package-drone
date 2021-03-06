/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Rathgeb - initial API and implementation
 *******************************************************************************/
package org.eclipse.packagedrone.repo.aspect.mvnosgi.internal;

import org.eclipse.packagedrone.repo.aspect.ChannelAspect;
import org.eclipse.packagedrone.repo.aspect.virtual.Virtualizer;

public class ChannelAspectImpl implements ChannelAspect
{

    @Override
    public String getId ()
    {
        return Constants.ASPECT_ID;
    }

    @Override
    public Virtualizer getArtifactVirtualizer ()
    {
        return new VirtualizerImpl ();
    }

}
