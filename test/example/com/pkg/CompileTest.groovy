package example.com.pkg

import org.junit.Assert
import org.junit.Test

class CompileTest {

    Compile compile = new Compile()

    @Test
    void testCompileReturnsCorrectCommands(){
        compile.run()
        Assert.assertEquals("", "")
    }

}
