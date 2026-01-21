package com.zenmo.vallum;

import com.zenmo.ztor.StopZtor;
import com.zenmo.zummon.companysurvey.Address;
import com.zenmo.zummon.companysurvey.GridConnection;
import com.zenmo.zummon.companysurvey.PandID;
import com.zenmo.zummon.companysurvey.Survey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import kotlinx.datetime.DateTimeUnit;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VallumTest {
    private static Vallum vallum;

    private static StopZtor stopZtor;

    @BeforeAll
    public static void beforeAll() {
        var port = 8083;
        stopZtor = InitKt.initZtor(port);

        vallum = new Vallum(
                System.getenv("CLIENT_ID"),
                System.getenv("CLIENT_SECRET"),
                "http://localhost:" + port,
                "https://keycloak.zenmo.com/realms/testrealm/protocol/openid-connect/token"
        );
    }

    @AfterAll
    public static void afterAll() {
        stopZtor.stop();
    }

    @Test
    public void test() {
        List<Survey> waardkwartierSurveys = vallum.getSurveysByProject(TestProjectNames.INSTANCE.getWAARDKWARTIER());
        assertEquals(1, waardkwartierSurveys.size());

        List<Survey> hessenwiekSurveys = vallum.getSurveysByProject(TestProjectNames.INSTANCE.getHESSENWIEK());
        // user does not have access to this project
        assertEquals(0, hessenwiekSurveys.size());


        var firstSurvey = waardkwartierSurveys.get(0);
        Address firstAddress = firstSurvey.getAddresses().get(0);
        int houseNumber = firstAddress.getHouseNumber();
        assertEquals(35, houseNumber);

        GridConnection gc = firstAddress.getGridConnections().get(0);
        Set<PandID> pandIds = gc.getPandIds();
        assertEquals("[PandID(value=1234567890123456), PandID(value=6543210987654321)]", pandIds.toString());

        var timeSeries = gc.getElectricity().getQuarterHourlyFeedIn_kWh();

        assertTrue(timeSeries.getValues().length > 0);

        // example usage
        for (var survey : waardkwartierSurveys) {
            for (var address : survey.getAddresses()) {
                for (var gridConnection : address.getGridConnections()) {
                    var pandIds2 = gridConnection.getPandIds();
                    var installedPv_kWp = gridConnection.getSupply().getPvInstalledKwp();
                    // hydrate model with vars
                }
            }
        }

//        Instant javaStart = timeSeries.getStart().toJavaInstant();
//        System.out.println(javaStart);
//        System.out.println(javaStart.isBefore(Instant.parse("2023-01-01T01:00:00+01:00")));
    }

    @Test
    public void testDateUnitComparison() {
        var year = DateTimeUnit.Companion.getYEAR();
        var twelveMonths = DateTimeUnit.Companion.getMONTH().times(12);

        assertTrue(year.equals(twelveMonths));
    }

    @Test
    public void testHourConversion() {
        DateTimeUnit timeStep = DateTimeUnit.Companion.getMINUTE().times(15);

        double hours = (double) ((DateTimeUnit.TimeBased) timeStep).getNanoseconds() / DateTimeUnit.Companion.getHOUR().getNanoseconds();

        assertEquals(0.25, hours);
    }

    @Test
    public void testGetProjectLastModifiedAt() {
        assertThrows(
                ProjectNotFound.class,
                () -> vallum.getProjectLastModifiedAt("doesnotexist")
        );

        var lastModifiedAt = vallum.getProjectLastModifiedAt(TestProjectNames.INSTANCE.getAPPELSCHA());
        assertNotNull(lastModifiedAt);
        assertTrue(lastModifiedAt.isBefore(Clock.systemUTC().instant()));
        assertTrue(lastModifiedAt.isAfter(Clock.systemUTC().instant().minus(1, ChronoUnit.HOURS)));
    }
}
