package org.apache.maven.shared.transfer.dependencies.resolve.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collection;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.filter.resolve.TransformableFilter;
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.transfer.dependencies.DependableCoordinate;
import org.apache.maven.shared.transfer.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.transfer.dependencies.resolve.DependencyResolverException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * 
 */
@Component( role = DependencyResolver.class, hint = "default" )
class DefaultDependencyResolver
    implements DependencyResolver, Contextualizable
{
    private PlexusContainer container;

    @Override
    public Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                         Collection<Dependency> coordinates,
                                                         Collection<Dependency> managedDependencies,
                                                         TransformableFilter filter )
        throws DependencyResolverException
    {
        validateBuildingRequest( buildingRequest );
        
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            DependencyResolver effectiveArtifactResolver = container.lookup( DependencyResolver.class, hint );

            return effectiveArtifactResolver.resolveDependencies( buildingRequest, coordinates, managedDependencies,
                                                                  filter );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyResolverException( e.getMessage(), e );
        }
    }

    @Override
    public Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest,
                                                         DependableCoordinate coordinate, TransformableFilter filter )
        throws DependencyResolverException
    {
        validateParameters( buildingRequest, coordinate, filter );
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            DependencyResolver effectiveArtifactResolver = container.lookup( DependencyResolver.class, hint );

            return effectiveArtifactResolver.resolveDependencies( buildingRequest, coordinate, filter );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyResolverException( e.getMessage(), e );
        }
    }

    @Override
    public Iterable<ArtifactResult> resolveDependencies( ProjectBuildingRequest buildingRequest, Model model,
                                                         TransformableFilter filter )
        throws DependencyResolverException
    {
        validateParameters( buildingRequest, model, filter );
        try
        {
            String hint = isMaven31() ? "maven31" : "maven3";

            DependencyResolver effectiveArtifactResolver = container.lookup( DependencyResolver.class, hint );

            return effectiveArtifactResolver.resolveDependencies( buildingRequest, model, filter );
        }
        catch ( ComponentLookupException e )
        {
            throw new DependencyResolverException( e.getMessage(), e );
        }
    }

    /**
     * @return true if the current Maven version is Maven 3.1.
     */
    private boolean isMaven31()
    {
        return canFindCoreClass( "org.eclipse.aether.artifact.Artifact" ); // Maven 3.1 specific
    }

    private boolean canFindCoreClass( String className )
    {
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( className );

            return true;
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }
    }

    /**
     * Injects the Plexus content.
     *
     * @param context Plexus context to inject.
     * @throws ContextException if the PlexusContainer could not be located.
     */
    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    private void validateParameters( ProjectBuildingRequest buildingRequest, DependableCoordinate coordinate,
                                     TransformableFilter filter )
    {
        validateBuildingRequest( buildingRequest );
        if ( coordinate == null )
        {
            throw new IllegalArgumentException( "The parameter coordinate is not allowed to be null." );
        }
        if ( filter == null )
        {
            throw new IllegalArgumentException( "The parameter filter is not allowed to be null." );
        }

    }

    private void validateParameters( ProjectBuildingRequest buildingRequest, Model model,
                                     TransformableFilter filter )
    {
        validateBuildingRequest( buildingRequest );
        if ( model == null )
        {
            throw new IllegalArgumentException( "The parameter model is not allowed to be null." );
        }
        if ( filter == null )
        {
            throw new IllegalArgumentException( "The parameter filter is not allowed to be null." );
        }

    }

    private void validateBuildingRequest( ProjectBuildingRequest buildingRequest )
    {
        if ( buildingRequest == null )
        {
            throw new IllegalArgumentException( "The parameter buildingRequest is not allowed to be null." );
        }
    }

}
