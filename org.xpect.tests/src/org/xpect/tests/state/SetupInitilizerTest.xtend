package org.xpect.tests.state

import org.junit.Test
import org.xpect.AbstractComponent
import org.xpect.setup.SetupInitializer

import static extension org.junit.Assert.*

class SetupInitilizerTest {
	
	
	@Test
	def void specificConstructorStrings() {
		val si = new SiT<Object>(null)
		val String[] strings = #["a","b"]
		val c = si.findConstructor(A,strings)
		A.assertSame( c.declaringClass )
		2.assertEquals( c.parameterCount )
	}	
	
	@Test
	def void varargConstructorStrings() {
		val si = new SiT<Object>(null)
		val String[] strings = #["a","b","c"]
		val c = si.findConstructor(A,strings)
		A.assertSame( c.declaringClass )
		1.assertEquals( c.parameterCount )
	}
	
	@Test(expected=RuntimeException)
	def void mixedVarargConstructor() {
		val si = new SiT<Object>(null)
		val Object[] objs = #[5.0,"b","c"]
		val c = si.findConstructor(A,objs)
		A.assertSame( c.declaringClass )
		1.assertEquals( c.parameterCount )
	}
	
	@Test
	def void varargConstructorStrings2() {
		val si = new SiT<Object>(null)
		val String[] strings = #["a"]
		val c = si.findConstructor(A,strings)
		A.assertSame( c.declaringClass )
		1.assertEquals( c.parameterCount )
		c.parameterTypes.get(0).isArray
	}
		
	@Test
	def void varargConstructorStrings_empty() {
		val si = new SiT<Object>(null)
		val String[] strings = #[]
		val c = si.findConstructor(A,strings)
		A.assertSame( c.declaringClass )
	}	
	
		
	@Test
	def void varargConstructor_double() {
		val si = new SiT<Object>(null)
		val Double[] dbls = #[1.3,4.0]
		val c = si.findConstructor(A,dbls)
		A.assertSame( c.declaringClass )
	}	
	
	static class A {
		new (Double ...d ){}
		new (String ...s ){}
		new (String a, String b){}
		new (String a, String ...s){}
		new (Double a, String ...s){}
	}
	
	static class SiT<T> extends SetupInitializer<T> {
	
		new(AbstractComponent rootInstance) {
			super(rootInstance)
		}
		
		override public findConstructor(Class<?> clazz, Object[] params) {
			super.findConstructor(clazz, params)
		}
		
	}	
	
}