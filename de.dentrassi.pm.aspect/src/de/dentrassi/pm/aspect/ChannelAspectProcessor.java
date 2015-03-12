/*******************************************************************************
 * Copyright (c) 2014, 2015 IBH SYSTEMS GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package de.dentrassi.pm.aspect;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

import de.dentrassi.pm.aspect.group.Group;
import de.dentrassi.pm.aspect.group.GroupInformation;
import de.dentrassi.pm.common.ChannelAspectInformation;

public class ChannelAspectProcessor
{

    private final static Logger logger = LoggerFactory.getLogger ( ChannelAspectProcessor.class );

    private final ServiceTracker<ChannelAspectFactory, FactoryEntry> tracker;

    private ServiceTracker<Group, GroupInformation> groupTracker;

    public static class FactoryEntry
    {

        private final ChannelAspectInformation information;

        private final ChannelAspectFactory service;

        public FactoryEntry ( final ChannelAspectInformation info, final ChannelAspectFactory service )
        {
            this.information = info;
            this.service = service;
        }

        public ChannelAspectInformation getInformation ()
        {
            return this.information;
        }

        public ChannelAspectFactory getService ()
        {
            return this.service;
        }

    }

    public ChannelAspectProcessor ( final BundleContext context )
    {

        this.tracker = new ServiceTracker<ChannelAspectFactory, FactoryEntry> ( context, ChannelAspectFactory.class, new ServiceTrackerCustomizer<ChannelAspectFactory, FactoryEntry> () {

            @Override
            public FactoryEntry addingService ( final ServiceReference<ChannelAspectFactory> reference )
            {
                return makeEntry ( context, reference );
            }

            @Override
            public void modifiedService ( final ServiceReference<ChannelAspectFactory> reference, final FactoryEntry service )
            {
            }

            @Override
            public void removedService ( final ServiceReference<ChannelAspectFactory> reference, final FactoryEntry service )
            {
                context.ungetService ( reference ); // makeEntry got the service
            }
        } );
        this.tracker.open ();

        this.groupTracker = new ServiceTracker<Group, GroupInformation> ( context, Group.class, new ServiceTrackerCustomizer<Group, GroupInformation> () {

            @Override
            public GroupInformation addingService ( final ServiceReference<Group> reference )
            {
                final Group group = context.getService ( reference );
                return group.getInformation ();
            }

            @Override
            public void modifiedService ( final ServiceReference<Group> reference, final GroupInformation service )
            {
            }

            @Override
            public void removedService ( final ServiceReference<Group> reference, final GroupInformation service )
            {
                context.ungetService ( reference );
            }
        } );
        this.groupTracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
        this.groupTracker.close ();
    }

    protected Map<String, ChannelAspectFactory> getAllFactories ()
    {
        final SortedMap<ServiceReference<ChannelAspectFactory>, FactoryEntry> tracked = this.tracker.getTracked ();

        final Map<String, ChannelAspectFactory> result = new HashMap<> ( tracked.size () );

        for ( final Map.Entry<ServiceReference<ChannelAspectFactory>, FactoryEntry> entry : tracked.entrySet () )
        {
            final Object key = entry.getKey ().getProperty ( ChannelAspectFactory.FACTORY_ID );
            if ( ! ( key instanceof String ) )
            {
                continue;
            }
            result.put ( (String)key, entry.getValue ().getService () );
        }

        return result;
    }

    public <T> void process ( final Collection<String> factoryIds, final Function<ChannelAspect, T> getter, final Consumer<T> consumer )
    {
        final Collection<ChannelAspect> aspects = createAspects ( factoryIds );
        for ( final ChannelAspect aspect : aspects )
        {
            final T t = getter.apply ( aspect );
            if ( t != null )
            {
                consumer.accept ( t );
            }
        }
    }

    public <T> void processWithAspect ( final Collection<String> factoryIds, final Function<ChannelAspect, T> getter, final BiConsumer<ChannelAspect, T> consumer )
    {
        final Collection<ChannelAspect> aspects = createAspects ( factoryIds );
        for ( final ChannelAspect aspect : aspects )
        {
            final T t = getter.apply ( aspect );
            if ( t != null )
            {
                consumer.accept ( aspect, t );
            }
        }
    }

    public void processWithAspects ( final Collection<String> factoryIds, final Consumer<ChannelAspect> aspectConsumer )
    {
        final Collection<ChannelAspect> aspects = createAspects ( factoryIds );
        for ( final ChannelAspect aspect : aspects )
        {
            aspectConsumer.accept ( aspect );
        }
    }

    private Collection<ChannelAspect> createAspects ( final Collection<String> factoryIds )
    {
        final List<String> missingAspects = new LinkedList<> ();

        final Map<String, ChannelAspectFactory> factories = getAllFactories ();

        final List<ChannelAspect> result = new ArrayList<ChannelAspect> ( factoryIds.size () );
        for ( final String id : factoryIds )
        {
            final ChannelAspectFactory factory = factories.get ( id );
            if ( factory == null )
            {
                missingAspects.add ( id );
            }
            else
            {
                final ChannelAspect aspect = factory.createAspect ();
                if ( aspect != null )
                {
                    result.add ( aspect );
                }
                else
                {
                    missingAspects.add ( id );
                }
            }
        }

        if ( !missingAspects.isEmpty () )
        {
            throw new IllegalStateException ( String.format ( "Missing aspects: %s", missingAspects ) );
        }

        return result;
    }

    public Map<String, ChannelAspectInformation> getAspectInformations ()
    {
        final Map<String, ChannelAspectInformation> result = new HashMap<> ();

        for ( final FactoryEntry entry : this.tracker.getTracked ().values () )
        {
            final ChannelAspectInformation info = entry.getInformation ();
            result.put ( info.getFactoryId (), info );
        }

        return result;
    }

    protected static FactoryEntry makeEntry ( final BundleContext context, final ServiceReference<ChannelAspectFactory> ref )
    {
        final String factoryId = getString ( ref, ChannelAspectFactory.FACTORY_ID, getString ( ref, Constants.SERVICE_PID, null ) );
        if ( factoryId == null )
        {
            return null;
        }

        final String label = getString ( ref, ChannelAspectFactory.NAME, null );

        final String descUrl = getString ( ref, ChannelAspectFactory.DESCRIPTION_FILE, null );
        final String description;
        if ( descUrl != null )
        {
            description = loadUrl ( ref.getBundle (), descUrl );
        }
        else
        {
            description = getString ( ref, ChannelAspectFactory.DESCRIPTION, getString ( ref, Constants.SERVICE_DESCRIPTION, null ) );
        }

        final String groupId = getString ( ref, ChannelAspectFactory.GROUP_ID, null );

        final SortedSet<String> requires = makeRequires ( ref );

        final ChannelAspectInformation info = new ChannelAspectInformation ( factoryId, label, description, groupId, requires );

        return new FactoryEntry ( info, context.getService ( ref ) );
    }

    private static SortedSet<String> makeRequires ( final ServiceReference<ChannelAspectFactory> ref )
    {
        final Object val = ref.getProperty ( ChannelAspectFactory.REQUIRES );

        if ( val instanceof String[] )
        {
            return new TreeSet<> ( Arrays.asList ( (String[])val ) );
        }
        if ( val instanceof String )
        {
            final String s = (String)val;
            return new TreeSet<> ( Arrays.asList ( s.split ( "[\\p{Space},]+" ) ) );
        }
        return null;
    }

    private static String loadUrl ( final Bundle bundle, final String descUrl )
    {
        final URL url = bundle.getEntry ( descUrl );
        if ( url == null )
        {
            return null;
        }

        try ( Reader reader = new InputStreamReader ( url.openStream (), StandardCharsets.UTF_8 ) )
        {
            return CharStreams.toString ( reader );
        }
        catch ( final Exception e )
        {
            logger.info ( "Failed to load url", e );
        }
        return null;
    }

    public List<ChannelAspectInformation> resolve ( final Collection<String> aspects )
    {
        final Map<String, ChannelAspectInformation> infos = getAspectInformations ();

        final List<ChannelAspectInformation> result = new ArrayList<ChannelAspectInformation> ( aspects.size () );

        for ( final String aspect : aspects )
        {
            final ChannelAspectInformation ai = infos.get ( aspect );
            if ( ai == null )
            {
                result.add ( ChannelAspectInformation.unresolved ( aspect ) );
            }
            else
            {
                result.add ( ai );
            }
        }

        return result;
    }

    private static String getString ( final ServiceReference<ChannelAspectFactory> ref, final String name, final String defaultValue )
    {
        final Object v = ref.getProperty ( name );
        if ( v == null )
        {
            return defaultValue;
        }
        return v.toString ();
    }

    public Collection<GroupInformation> getGroups ()
    {
        return new ArrayList<> ( this.groupTracker.getTracked ().values () );
    }

}
