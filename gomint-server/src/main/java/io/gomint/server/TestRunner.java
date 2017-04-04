package io.gomint.server;

import io.gomint.event.CancelableEvent;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.server.event.EventProxy;
import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TestRunner {

    public static void main( String[] args ) {
        EventListener listener = new EventListener() {
            @EventHandler
            public void onPlayerJoin( CancelableEvent event ) {

            }
        };

        try {
            Method method = listener.getClass().getDeclaredMethod( "onPlayerJoin", CancelableEvent.class );
            method.setAccessible( true );

            for ( int i = 0; i < 5; i++ ) {
                for ( int x = 0; x < 50000000; x++ ) {
                    method.invoke( listener, new CancelableEvent() );
                }
            }

            long start = System.currentTimeMillis();

            for ( int i = 0; i < 50000000; i++ ) {
                method.invoke( listener, new CancelableEvent() );
            }

            System.out.println( "Reflection took " + ( System.currentTimeMillis() - start ) + "ms" );
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }


        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass( "io.gomint.server.TestEventProxy" );
            ctClass.addInterface( pool.get( "io.gomint.server.EventProxy" ) );
            ctClass.addField( CtField.make( "public " + listener.getClass().getName() + " obj;", ctClass ) );
            ctClass.addMethod( CtMethod.make( "public void call( io.gomint.event.Event e ) { obj.onPlayerJoin( (io.gomint.event.CancelableEvent) e ); }", ctClass ) );

            EventProxy proxy = (EventProxy) ctClass.toClass().newInstance();
            proxy.getClass().getDeclaredField( "obj" ).set( proxy, listener );

            for ( int i = 0; i < 5; i++ ) {
                for ( int x = 0; x < 50000000; x++ ) {
                    proxy.call( new CancelableEvent() );
                }
            }

            long start = System.currentTimeMillis();

            for ( int i = 0; i < 50000000; i++ ) {
                proxy.call( new CancelableEvent() );
            }

            System.out.println( "Proxy took " + ( System.currentTimeMillis() - start ) + "ms" );
        } catch ( NotFoundException | CannotCompileException | IllegalAccessException | InstantiationException | NoSuchFieldException e ) {
            e.printStackTrace();
        }
    }

}
