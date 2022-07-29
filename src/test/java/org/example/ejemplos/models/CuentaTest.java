package org.example.ejemplos.models;

import org.example.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {

    private TestInfo testInfo;
    private TestReporter testReporter;

    // cada vez que se inicia un nuevo metodo test este metodo se iniciara otra vez desde 0
    // por lo que quedaria limpio y si se repitieran cosas podrian declararse de forma global y luego solo editar
    // esa referencia de objeto instanciado
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el metodo.");
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando test unitario");
    }

    // los all estan asociados a la clase y no a la instancia por eso son static, si se cambian fallara
    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando los tests unitarios");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Fin Tests Unitarios");
    }

    @Tag("Cuenta")
    @Test
    // Cambia el nombre con el que se muestra la prueba unitaria en vez de mostrar el metodo de la prueba unitaria
    @DisplayName("Probando el nombre de la cuenta \uD83E\uDD21")
    void TestAccountName() {
        testReporter.publishEntry(testInfo.getTags().toString());
        // de esta forma si se necesita realizar algo en especifico por cada tag se puede utilizar estas variables
        if (testInfo.getTags().contains("Cuenta")) {
            testReporter.publishEntry("hacer algo con la etiqueta cuenta");
        }
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.1234"));
        String esperado = "Ruben";
        String real = cuenta.getPersona();
        assertEquals(esperado, real);
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // negada a la de arriba
    }

    // Comparando por distinta referencia y por valor
    @Test
    void testReferences() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("12333.232"));
        Cuenta cuenta2 = new Cuenta("kan doe", new BigDecimal("12333.232"));
        // Pasa porque no son iguales en referencias
        //assertNotEquals(cuenta, cuenta2);
        // Para comparar por atributo y su valor se debe modificar el comparador de la clase
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.232", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.232", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Chulin Doe", new BigDecimal("1000.232"));

        Banco banco = new Banco("Banco De Chile");
        banco.trasnferir(cuenta2, cuenta, new BigDecimal(500));

        assertEquals("500.232", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Chulin Doe", new BigDecimal("1000.232"));

        Banco banco = new Banco("Banco De Chile");
        banco.addCuentas(cuenta);
        banco.addCuentas(cuenta2);

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Banco De Chile", cuenta.getBanco().getNombreBanco());
        assertEquals("Chulin Doe", banco.getCuentas().stream()
                .filter(c -> c.getPersona()
                        .equals("Chulin Doe"))
                .findFirst().get().getPersona()
        );
        assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getPersona()
                        .equals("Chulin Doe"))
                .findFirst().isPresent()
        );
        assertTrue(banco.getCuentas().stream() // busca si cualquiera cumple con algo y no sigue NO VALIDA QUE TODOS TENGAN ALGO,
                // SOLO BUSCA EL PRIMER MATCH SI ESTA ES TRUE Y LUEGO SALE
                .anyMatch(c -> c.getPersona()
                        .equals("Chulin Doe"))
        );

        banco.trasnferir(cuenta2, cuenta, new BigDecimal(500));

        assertEquals("500.232", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    // @Disabled gestionara que se salte esta prueba
    @Disabled
    void testAssertAll() {
        //fail(); fuerza el error
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Chulin Doe", new BigDecimal("1000.232"));

        Banco banco = new Banco("Banco De Chile");
        banco.addCuentas(cuenta);
        banco.addCuentas(cuenta2);
        // Se defienen dentro de assertAll con funcion lambda
        banco.trasnferir(cuenta2, cuenta, new BigDecimal(500));
        assertAll(
                () -> {
                    assertEquals(3, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco De Chile,", cuenta.getBanco().getNombreBanco());
                },
                () -> {
                    assertEquals("Chulin Doe", banco.getCuentas().stream()
                            .filter(c -> c.getPersona()
                                    .equals("Chulin Doe"))
                            .findFirst().get().getPersona()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona()
                                    .equals("Chulin Doez"))
                            .findFirst().isPresent()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream() // busca si cualquiera cumple con algo y no sigue NO VALIDA QUE TODOS TENGAN ALGO,
                            // SOLO BUSCA EL PRIMER MATCH SI ESTA ES TRUE Y LUEGO SALE
                            .anyMatch(c -> c.getPersona()
                                    .equals("Chulin Doe"))
                    );
                },
                () -> {
                    assertEquals("500.232", cuenta2.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals("3000", cuenta.getSaldo().toPlainString());
                }
        );
    }

    @Test
    void testAssertAll2() {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Chulin Doe", new BigDecimal("1000.232"));

        Banco banco = new Banco("Banco De Chile");
        banco.addCuentas(cuenta);
        banco.addCuentas(cuenta2);
        // Se defienen dentro de assertAll con funcion lambda
        banco.trasnferir(cuenta2, cuenta, new BigDecimal(500));
        assertAll(
                () -> { // el ultimo parametro es para darle un mensaje personalizaqdo a la prueba unitaria
                    assertEquals(2, banco.getCuentas().size(), "Se ingresaron solo 2 cuentas no deberian haber más ni menos");
                },
                () -> {
                    // Al usar la expresion lambda impedimos que se genera la instacia del string, ya que solo se generara si salta el error, impidiendo el uso execivo de recursos
                    assertEquals("Banco De Chile", cuenta.getBanco().getNombreBanco(), () -> "error el banco no es el esperado, se esperaba Banco De Chile pero el real era: " +
                            cuenta.getBanco().getNombreBanco());
                },
                () -> {
                    assertEquals("Chulin Doe", banco.getCuentas().stream()
                            .filter(c -> c.getPersona()
                                    .equals("Chulin Doe"))
                            .findFirst().get().getPersona()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona()
                                    .equals("Chulin Doe"))
                            .findFirst().isPresent()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream() // busca si cualquiera cumple con algo y no sigue NO VALIDA QUE TODOS TENGAN ALGO,
                            // SOLO BUSCA EL PRIMER MATCH SI ESTA ES TRUE Y LUEGO SALE
                            .anyMatch(c -> c.getPersona()
                                    .equals("Chulin Doe"))
                    );
                },
                () -> {
                    assertEquals("500.232", cuenta2.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals("3000", cuenta.getSaldo().toPlainString());
                }
        );
    }

    // @Nested = inner class clase adentro de otra clase
    // no hay una regla de oro que explique como usar nested pero es suficiente con usuarlo para
    // darle una categorizacion a las pruebas unitari
    @Nested
    @DisplayName("Validar Sistemas Operativos")
    class TestWindows {
        // Con esta notacionm podemos indicarle la condiciona de que sistemas operativos queremos involucar en el test
        // unitario
        @Test
        @DisplayName("Windows")
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
            assertTrue(true);
        }

        // Se podria usar @DisableOnOs para decir en que windows no habilitar

        @Test
        @DisplayName("MAC O LINUX")
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void testSoloMacLinux() {
            assertTrue(true);
        }
    }

    @Nested
    class TestJRE {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void java8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void java11() {
        }
    }

    @Nested
    class TestPropiedadesSistema {
        // si existe ciertas propiedades del sistema
        // Se puede trabajar con expresiones regulares tambien en el matches
        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "1.8.0_111")
        void propiedadesSistema() {
            Properties properties = System.getProperties();
            properties.forEach((key, value) -> System.out.println(key + ":" + value));
        }

        @Test
        //@EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64Bits() {
        }
    }

    @Nested
    class TestVariablesAmbiente {
        //Variables de ambiente del sistema operativo

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-18.0.1.1.*")
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((key, value) -> System.out.println(key + ":" + value));
        }

        // Assumptions sirven para programar un booleando de forma programatica, por ejemplo si necesito habilitar o
        // deshabilitar una prueba unitaria
        // de acuerdo a alguna variable booleana del codigo como tal
    }


    @Test
    void testSaldoCuentaDev() {
        // Validar que exista en las configuraciones del arraque este string DEV
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        // De esta forma todo lo que viene abajo del assumtion se ejecutara o no en funcion del booleano
        assumeTrue(esDev);
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // negada a la de arriba
    }

    @Test
    void testSaldoCuentaDev2() {
        // Validar que exista en las configuraciones del arraque este string DEV
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        // De esta forma todo lo que viene abajo del assumtion se ejecutara o no en funcion del booleano
        // Si el asuming no es true, no entrara al lambda y solo se ejecutarian los que estan afuera
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.12345"));
        assumingThat(esDev, () -> {

            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // negada a la de arriba
    }

    // se puede definir el numero de repeticiones
    // tambien podemos cambiar como se mostraran las repeticiones pasandole los valores
    // @RepeatedTest(5)
    @RepeatedTest(value = 5, name = "Rep {currentRepetition} de {totalRepetitions}")
    void testSaldoCuentaDevRepetitivo() {
        // Validar que exista en las configuraciones del arraque este string DEV
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        // De esta forma todo lo que viene abajo del assumtion se ejecutara o no en funcion del booleano
        // Si el asuming no es true, no entrara al lambda y solo se ejecutarian los que estan afuera
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.12345"));
        assumingThat(esDev, () -> {

            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // negada a la de arriba
    }

    @RepeatedTest(value = 5, name = "Rep {currentRepetition} de {totalRepetitions}")
    void testSaldoCuentaDevRepetitivoInfo(RepetitionInfo info) {
        // Recibe el parametro d elas repeticiones para poder cambiar de forma programatica en cada iteracion algo del test
        // como si fuera el index de un array
        if (info.getCurrentRepetition() == 3) {
            System.out.println("repeticion numero " + info.getCurrentRepetition());
        }
        // Validar que exista en las configuraciones del arraque este string DEV
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        // De esta forma todo lo que viene abajo del assumtion se ejecutara o no en funcion del booleano
        // Si el asuming no es true, no entrara al lambda y solo se ejecutarian los que estan afuera
        Cuenta cuenta = new Cuenta("Ruben", new BigDecimal("1000.12345"));
        assumingThat(esDev, () -> {

            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });

        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0); // negada a la de arriba
    }

    // Al colocarlo en la clase, se le agrega esta etiqueta a todas las pruegas dentro de la clase Nested
    @Tag("pruebasParametizadas")
    @Nested
    class PruebasParametizadasTest {


        // Test debito params
        //@ParameterizedTest
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0}")
        // Tambien se le peuden pasar parametros de Csvfile separados por,
        @ValueSource(strings = {"100", "200", "300", "1200.231"})
        void testDebitoCuentaParametrizado(String monto) {
            Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


        // se le asignan varios valores y le asiganamos su llave
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    // recibe un metodo para parametrizar los valores, con el tag luego podemos decir en la configuracion de ejecucion cuales se
    // va a ejecutar y cuales no
    @Tag("pruebasParametizadas")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        Cuenta cuenta = new Cuenta("kan doe", new BigDecimal("1000.232"));
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    //Deberia ser estatico en la raiz de la clase pero para arreglar el codigo lo deje aqui
    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        // Por defecto la unidad de tiempo es Segundos pero se podria cambiare asi
        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            //Realiza una pausa
            TimeUnit.MILLISECONDS.sleep(1100);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }
}
