package uk.gov.companieshouse.acsp.models.filing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTests {

    @Test
    void correctlySetsAndGetsAddressLine1() {
        Address address = new Address();
        address.setAddressLine1("123 Main Street");
        assertEquals("123 Main Street", address.getAddressLine1());
    }

    @Test
    void correctlySetsAndGetsAddressLine2() {
        Address address = new Address();
        address.setAddressLine2("Apt 4B");
        assertEquals("Apt 4B", address.getAddressLine2());
    }

    @Test
    void correctlySetsAndGetsCountry() {
        Address address = new Address();
        address.setCountry("United Kingdom");
        assertEquals("United Kingdom", address.getCountry());
    }

    @Test
    void correctlySetsAndGetsLocality() {
        Address address = new Address();
        address.setLocality("London");
        assertEquals("London", address.getLocality());
    }

    @Test
    void correctlySetsAndGetsPoBox() {
        Address address = new Address();
        address.setPoBox("PO123");
        assertEquals("PO123", address.getPoBox());
    }

    @Test
    void correctlySetsAndGetsPostalCode() {
        Address address = new Address();
        address.setPostalCode("SW1A 1AA");
        assertEquals("SW1A 1AA", address.getPostalCode());
    }

    @Test
    void correctlySetsAndGetsPremises() {
        Address address = new Address();
        address.setPremises("10 Downing Street");
        assertEquals("10 Downing Street", address.getPremises());
    }

    @Test
    void correctlySetsAndGetsRegion() {
        Address address = new Address();
        address.setRegion("Greater London");
        assertEquals("Greater London", address.getRegion());
    }

    @Test
    void returnsNullForUnsetFields() {
        Address address = new Address();
        assertNull(address.getAddressLine1());
        assertNull(address.getAddressLine2());
        assertNull(address.getCountry());
        assertNull(address.getLocality());
        assertNull(address.getPoBox());
        assertNull(address.getPostalCode());
        assertNull(address.getPremises());
        assertNull(address.getRegion());
    }
}