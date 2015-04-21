// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cloudera.maven.validator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.cloudera.cli.validator.ApplicationConfiguration;
import com.cloudera.cli.validator.ValidationRunner;
import com.google.common.collect.ImmutableList;


@Mojo( name = "validate", defaultPhase = LifecyclePhase.TEST, threadSafe = true )
public class SchemaValidatorMojo extends AbstractMojo
{

    @Parameter( defaultValue = "src", required = true )
    private File sourceDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        List<String> sdlPaths = getServiceFiles();

        ApplicationContext ctx = new AnnotationConfigApplicationContext( ApplicationConfiguration.class );
        ValidationRunner runner = ctx.getBean( "sdlRunner", ValidationRunner.class );

        boolean errors = false;

        for( String sdl : sdlPaths ) {
            errors |= validate( runner, new File( sourceDirectory, sdl ) );
        }

        if( errors ) {
            throw new MojoFailureException( "Invalid SDL" );
        }
    }


    private boolean validate( ValidationRunner runner, File file ) throws MojoFailureException,
        MojoExecutionException
    {
        try {
            StringWriter writer = new StringWriter();
            if( !runner.run( file.getPath(), writer ) ) {
                getLog().error( writer.toString().trim() );
                return true;
            } else {
                getLog().info( writer.toString().trim() );
                return false;
            }
        } catch( IOException e ) {
            throw new MojoExecutionException( e.getMessage() );
        }
    }


    private List<String> getServiceFiles()
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( new String[] { "**\\*.sdl" } );
        scanner.setBasedir( sourceDirectory );
        scanner.scan();

        return ImmutableList.copyOf( scanner.getIncludedFiles() );
    }

}