/*******************************************************************************
 * Copyright (c) 2015 Jens Reimann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.pm.deb.aspect;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dentrassi.pm.aspect.aggregate.AggregationContext;
import de.dentrassi.pm.aspect.aggregate.ChannelAggregator;
import de.dentrassi.pm.common.ArtifactInformation;
import de.dentrassi.pm.common.MetaKey;
import de.dentrassi.pm.common.MetaKeys;
import de.dentrassi.pm.deb.ChannelConfiguration;
import de.dentrassi.pm.deb.aspect.RepoBuilder.PackageInformation;

public class AptAggregator implements ChannelAggregator
{
    @Override
    public String getId ()
    {
        return AptChannelAspectFactory.ID;
    }

    @Override
    public Map<String, String> aggregateMetaData ( final AggregationContext context ) throws Exception
    {
        final Map<String, String> result = new HashMap<> ();

        final Map<MetaKey, String> md = context.getChannelMetaData ();

        final ChannelConfiguration cfg = MetaKeys.bind ( new ChannelConfiguration (), md );

        if ( !cfg.isValid () )
        {
            return null;
        }

        final Gson gson = new GsonBuilder ().create ();

        final RepoBuilder repo = new RepoBuilder ();

        final DistributionInformation info = new DistributionInformation ();
        info.setArchitectures ( new TreeSet<> ( cfg.getArchitectures () ) );
        info.setComponents ( new TreeSet<> ( cfg.getComponents () ) );
        info.setVersion ( cfg.getVersion () );
        info.setDescription ( cfg.getDescription () );
        info.setLabel ( cfg.getLabel () );
        info.setSuite ( cfg.getSuite () );
        info.setCodename ( cfg.getCodename () );
        info.setOrigin ( cfg.getOrigin () );

        repo.addDistribution ( cfg.getDistribution (), info );

        for ( final ArtifactInformation art : context.getArtifacts () )
        {
            final String arch = art.getMetaData ().get ( new MetaKey ( DebianChannelAspectFactory.ID, "architecture" ) );
            final String packageName = art.getMetaData ().get ( new MetaKey ( DebianChannelAspectFactory.ID, "package" ) );
            final String version = art.getMetaData ().get ( new MetaKey ( DebianChannelAspectFactory.ID, "version" ) );
            final String controlJson = art.getMetaData ().get ( new MetaKey ( DebianChannelAspectFactory.ID, "control.json" ) );

            if ( arch == null || version == null || packageName == null || controlJson == null )
            {
                continue;
            }

            final ControlInformation control = gson.fromJson ( controlJson, ControlInformation.class );

            final PackageInformation packageInfo = new PackageInformation ( makePoolName ( art, packageName, version, arch ), art.getSize (), control.getValues () );
            repo.addPackage ( cfg.getDistribution (), cfg.getDefaultComponent (), arch, packageInfo );
        }

        repo.spoolOut ( ( name, mimeType, stream ) -> {
            addDistFile ( result, name, mimeType, ByteStreams.toByteArray ( stream ) );
        } );

        return result;
    }

    private String makePoolName ( final ArtifactInformation art, final String packageName, final String version, final String arch )
    {
        return String.format ( "pool/%s/%s_%s_%s.deb", art.getId (), packageName, version, arch );
    }

    private void addDistFile ( final Map<String, String> result, final String name, final String mimeType, final byte[] data )
    {
        result.put ( name, Base64.getEncoder ().encodeToString ( data ) );
        if ( "text/plain".equals ( mimeType ) )
        {
            result.put ( name + "#text", StandardCharsets.UTF_8.decode ( ByteBuffer.wrap ( data ) ).toString () );
        }
    }

}