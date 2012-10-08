package org.jboss.aerogear.controller;

public class SampleController {

    public void index() {
    }

    public void client(String name) {
    }

    public void lol() {
    }

    public void save(RoutesTest.Car car) {

    }

    public void find(String id) {
    }

    public void admin() {
    }
    
    public void error(final Exception e) {
    }

    public void throwSampleControllerException() throws SampleControllerException {
        throw new SampleControllerException("Bogus exception");
    }
    
    public void throwIllegalStateException() {
        throw new IllegalStateException("Bogus exception");
    }

    public void errorPage() {
    }
}
