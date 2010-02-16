package org.apache.maven.archiva.repository.audit;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.archiva.metadata.model.MetadataFacet;

/**
 * AuditEvent
 *
 * @version $Id$
 */
public class AuditEvent
    implements MetadataFacet
{
    public static final String TIMESTAMP_FORMAT = "yyyy/MM/dd/HHmmss.SSS";

    public static final String CREATE_DIR = "Created Directory";

    public static final String CREATE_FILE = "Created File";

    public static final String REMOVE_DIR = "Removed Directory";

    public static final String REMOVE_FILE = "Removed File";

    public static final String MODIFY_FILE = "Modified File";

    public static final String MOVE_FILE = "Moved File";

    public static final String MOVE_DIRECTORY = "Moved Directory";

    public static final String COPY_DIRECTORY = "Copied Directory";

    public static final String COPY_FILE = "Copied File";

    public static final String UPLOAD_FILE = "Uploaded File";

    public static final String ADD_LEGACY_PATH = "Added Legacy Artifact Path";

    public static final String REMOVE_LEGACY_PATH = "Removed Legacy Artifact Path";

    public static final String PURGE_ARTIFACT = "Purged Artifact";

    public static final String PURGE_FILE = "Purged Support File";

    public static final String REMOVE_SCANNED = "Removed in Filesystem";

    // configuration events

    public static final String ADD_MANAGED_REPO = "Added Managed Repository";

    public static final String MODIFY_MANAGED_REPO = "Updated Managed Repository";

    public static final String DELETE_MANAGED_REPO = "Deleted Managed Repository";

    public static final String ADD_REMOTE_REPO = "Added Remote Repository";

    public static final String MODIFY_REMOTE_REPO = "Updated Remote Repository";

    public static final String DELETE_REMOTE_REPO = "Deleted Remote Repository";

    public static final String ADD_REPO_GROUP = "Added Repository Group";

    public static final String DELETE_REPO_GROUP = "Deleted Repository Group";

    public static final String ADD_REPO_TO_GROUP = "Added Repository to Group";

    public static final String DELETE_REPO_FROM_GROUP = "Deleted Repository from Group";

    public static final String ENABLE_REPO_CONSUMER = "Enabled Content Consumer";

    public static final String DISABLE_REPO_CONSUMER = "Disabled Content Consumer";

    public static final String ADD_PATTERN = "Added File Type Pattern";

    public static final String REMOVE_PATTERN = "Removed File Type Pattern";

    private String repositoryId;

    private String userId;

    private String remoteIP;

    // TODO: change to artifact reference? does it ever refer to just a path?

    private String resource;

    private String action;

    private Date timestamp;

    public static final String FACET_ID = "org.apache.archiva.audit";

    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone( "UTC" );

    public AuditEvent()
    {
        /* do nothing */
    }

    public AuditEvent( String name, String repositoryId )
    {
        try
        {
            int index = name.lastIndexOf( '/' );
            String ts = index > 0 ? name.substring( 0, index ) : name;
            timestamp = createNameFormat().parse( ts );
        }
        catch ( ParseException e )
        {
            throw new IllegalArgumentException( "Improperly formatted timestamp for audit log event: " + name );
        }
        this.repositoryId = repositoryId;
    }

    public AuditEvent( String repoId, String user, String resource, String action )
    {
        this.repositoryId = repoId;
        this.userId = user;
        this.resource = resource;
        this.action = action;
        this.timestamp = Calendar.getInstance().getTime();
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId( String repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getResource()
    {
        return resource;
    }

    public void setResource( String resource )
    {
        this.resource = resource;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction( String action )
    {
        this.action = action;
    }

    public String getRemoteIP()
    {
        return remoteIP;
    }

    public void setRemoteIP( String remoteIP )
    {
        this.remoteIP = remoteIP;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getFacetId()
    {
        return FACET_ID;
    }

    public String getName()
    {
        // we add the resource name to the end to avoid clashes at that timestamp
        // TODO: this is still not robust enough and the content model should be revised
        return createNameFormat().format( timestamp ) + "/" + resource.substring( resource.lastIndexOf( '/' ) + 1 );
    }

    private static SimpleDateFormat createNameFormat()
    {
        SimpleDateFormat fmt = new SimpleDateFormat( TIMESTAMP_FORMAT );
        fmt.setTimeZone( UTC_TIME_ZONE );
        return fmt;
    }

    public Map<String, String> toProperties()
    {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "action", this.action );
        if ( this.userId != null )
        {
            properties.put( "user", this.userId );
        }
        if ( this.remoteIP != null )
        {
            properties.put( "remoteIP", this.remoteIP );
        }
        if ( this.resource != null )
        {
            properties.put( "resource", this.resource );
        }
        return properties;
    }

    public void fromProperties( Map<String, String> properties )
    {
        this.action = properties.get( "action" );
        this.remoteIP = properties.get( "remoteIP" );
        this.userId = properties.get( "user" );
        this.resource = properties.get( "resource" );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        AuditEvent that = (AuditEvent) o;

        if ( !action.equals( that.action ) )
        {
            return false;
        }
        if ( remoteIP != null ? !remoteIP.equals( that.remoteIP ) : that.remoteIP != null )
        {
            return false;
        }
        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }
        if ( resource != null ? !resource.equals( that.resource ) : that.resource != null )
        {
            return false;
        }
        if ( !timestamp.equals( that.timestamp ) )
        {
            return false;
        }
        if ( userId != null ? !userId.equals( that.userId ) : that.userId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( userId != null ? userId.hashCode() : 0 );
        result = 31 * result + ( remoteIP != null ? remoteIP.hashCode() : 0 );
        result = 31 * result + ( resource != null ? resource.hashCode() : 0 );
        result = 31 * result + action.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "AuditEvent{" + "repositoryId='" + repositoryId + '\'' + ", userId='" + userId + '\'' + ", remoteIP='" +
            remoteIP + '\'' + ", resource='" + resource + '\'' + ", action='" + action + '\'' + ", timestamp=" +
            timestamp + '}';
    }
}
