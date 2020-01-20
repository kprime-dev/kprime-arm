# vertical specification:

 <decompose>
	<table tableName>
		<cols>
	<constraint fun dep>
		<source><cols>
		<target><cols>
	<changeSet>
 	 <xpath name="K">tableName key cols</xpath>
	 <xpath name="LHS">tableName source fun dep cols</xpath>
	 <xpath name="RHS">tableName target fun dep cols</xpath>
	 <xpath name="Rest">tableName other cols=*-K-LHS-RHS</xpath>
  	 <createView tableName1>select K,LHS,Rest from tableName</createView>
	 <createView tableName2>select LHS,RHS from tableName</createView>
        <constraint inclusion>
            <source tableName1><col>
            <target tableName2><col>
        <constraint inclusion>
            <source tableName2><col>
            <target tableName1><col>
	<changeSet>

  <compose>
	<table tableName1>
		<cols>
	<table tableName2>
		<cols>
	<constraint inclusion>
		<source tableName1><col>
		<target tableName2><col>
	<constraint inclusion>
		<source tableName2><col>
		<target tableName1><col>
	<xpath name="K">key cols</xpath>
	<xpath name="LHS">source fun dep cols</xpath>
	<xpath name="RHS">target fun dep cols</xpath>
	<xpath name="Rest">other cols</xpath>
	<changeSet>
  	 <createView tableName>select * from tableName1 join tableName2 on inclusion col </createView>
        <constraint fun dep>
            <source><cols>
            <target><cols>
	<changeSet>
