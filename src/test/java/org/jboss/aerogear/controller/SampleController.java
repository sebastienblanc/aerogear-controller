package org.jboss.aerogear.controller;

public class SampleController {

    public void index() {
    }

    public void client(String name) {
    }

    public void lol() {
    }

    public void save(Car car) {
    }
    
    public void save(String color, String brand) {
    }
    
    public void save(Car car, String metadata) {
    }

    public void find(String id) {
    }
    
    public void find(String color, String brand) {
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
    
    public void superException() {
    }
    
    public void subException() {
    }
}
