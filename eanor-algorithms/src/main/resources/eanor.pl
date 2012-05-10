% Autor: Robert J. Corujo
% Fecha: 18/05/2009

prueba(X,X).


%-----------pertenece(Elemento,Lista)-----------
%   Nos dice si elemento pertenece a la Lista
pertenece(_,[]) :- !, fail.
pertenece(X,[X|_]):- !.
pertenece(X,[_|T]) :-
                   pertenece(X,T).
                   
%----------desglosa_lista(Lista,Elemento_actual) -----
%         Cada vez devuelve un elemento distinto
desglosa_lista([],_) :- !, fail.

desglosa_lista([H|_],H).

desglosa_lista([_|T],H2) :-     desglosa_lista(T,H2).


                   
%------------subcjto(C1,C2)----------------
%    Comprueba si C1 es sub cjto de C2
subcjto([],_).
subcjto([X|T],Csup) :-
                    pertenece(X,Csup),
                    subcjto(T,Csup), !.
subcjto(_, _) :- !, fail.



%-------------------------------union_cjto(C1,C2,C3)----------------------------------
%    Añade elementos de C2 en C1 siempre y cuando no estén ya y lo devuelve en C3
union_cjto(C1,[],C1) :- !.
union_cjto(C1,[X|T],C2) :-
                     pertenece(X,C1),
                     union_cjto(C1,T,C2), !.
union_cjto(C1,[X|T],C2) :-
                        append(C1,[X],Caux),
                        union_cjto(Caux,T,C2).
                        
                        

%-------------------------------dif_cjto(C1,C2,C3)----------------------------------
%                Calcula C3 como la diferencia de cjtos C1 - C2                    *
%-----------------------------------------------------------------------------------
dif_cjto([],_,[]) :- !.
dif_cjto([H|T],C2,C3) :-
                        pertenece(H,C2),
                        dif_cjto(T,C2,C3), !.
dif_cjto([H|T],C2,C3) :-
                        dif_cjto(T,C2,C3aux),
                        append([H],C3aux,C3),!.



%______________________________________________________________________________________________________________________

cierre_ac(Ac,[],_,Ac) :- !.
cierre_ac(C1,[dep(C2,L)|T],TotalDep,Cierre) :-
                                       subcjto(C2,C1),                        %C2 es subcjto de C1
                                       union_cjto(C1,L,C3),
                                       (C1 \= C3, cierre_ac(C3,TotalDep,TotalDep,Cierre),!); (cierre_ac(C1,T,TotalDep,Cierre),!).
cierre_ac(C1,[_|T],TotalDep,Cierre) :-
                             cierre_ac(C1,T,TotalDep,Cierre).


%-------------------------------cierre(C1,L,Cierre)---------------------------------
%  Cierre de cjto de atributos C1 con respecto a las dependencias funcionales en L
%-----------------------------------------------------------------------------------
cierre(C1,L,Cierre) :-
                    append([],C1, Ac),
                    cierre_ac(Ac,L,L,Cierre).




%--------------------------desglosa_df(Antecedente,Consecuente,Total)-------------------------------------
%        crea varias dependencias funcionales tal que el antecedente es el mismo
%        y el consecuente es cada elemento de Consecuente. Así se consiguen
%        dependencias con consecuentes unitarios.
%---------------------------------------------------------------------------------------------------------

desglosa_df(_,[],[]) :- !.
desglosa_df(L1,[X|T],Total) :-
                            desglosa_df(L1,T,Aux),
                            append([dep(L1,[X])],Aux,Total).

%Las dependencias funcionales de consecuentes > 1 las desglosa en dependencias con un único consecuente
prepara_df([],[]) :- !.
prepara_df([dep(L1,L2)|T],Newdf) :-
                                 desglosa_df(L1,L2,Nuevasdf),
                                 prepara_df(T,Sigdf),
                                 append(Nuevasdf,Sigdf,Newdf).
                                 
                                 
                                 


%--------------------------es_redundante(Dependencia,ListaDeps)-------------------------------------
%        Comprueba si una dependencia es redudante con respecto a una lista de dependencias
%---------------------------------------------------------------------------------------------------

es_redundante(dep(X,[Y]),Totaldf) :-
                                  cierre(X,Totaldf,Cierre),
                                  pertenece(Y,Cierre).


elim_red_ac([],Dependencias,Dependencias).
elim_red_ac(DepRestantes,DepProcesadas,Nuevasdf) :-
                                              [Dep|Cola] = DepRestantes,
                                              append(DepProcesadas,Cola,Depdif),
                                              es_redundante(Dep,Depdif), !,
                                              elim_red_ac(Cola,DepProcesadas,Nuevasdf), !.
%Sabemos que no es redundante
elim_red_ac(DepRestantes,DepProcesadas, Nuevasdf) :-
                                              [Dep|Cola] = DepRestantes,
                                              append(DepProcesadas,[Dep],NuevasProcesadas),
                                              elim_red_ac(Cola,NuevasProcesadas,Nuevasdf), !.
                                              
%--------------------------elimina_redundantes(DepInit,DepFin)------------------------
%        Recibe el cjto de dependencias y devuelve el cjto de dependencias
%        habiendo eliminado las dependencias redundantes
%-------------------------------------------------------------------------------------

elimina_redundantes(DepInit,DepFin) :-
                                    elim_red_ac(DepInit,[],DepFin).



%______________________________________________________________________________________________________________________

%Predicados auxiliares para elimina_atribs_raros

check_atribs([],Checked,_,_,Checked) :- !.
check_atribs([_|T],Checked,Consecuente,TotalDeps,NoRaros) :-
                                                      append(Checked,T,NuevoAnt),
                                                      cierre(NuevoAnt,TotalDeps,Cierre),
                                                      Consecuente = [Consec],
                                                      pertenece(Consec,Cierre),           %En este caso el atributo es raro debe eliminarse del antecedente
                                                      check_atribs(T,Checked,Consecuente,TotalDeps,NoRaros), !.
%El atrib NO es raro
check_atribs([Atrib|T],Checked,Consecuente,TotalDeps,NoRaros) :-
                                                              append(Checked,[Atrib],NewChecked),
                                                              check_atribs(T,NewChecked,Consecuente,TotalDeps,NoRaros).

elim_atrib_raro([],DepFin,DepFin).
elim_atrib_raro(DepRestantes, DepProcesadas, Nuevasdf) :-
                                                       [Dep|Cola] = DepRestantes,
                                                       %Comprobamos que es antecedente compuesto
                                                       Dep = dep([X|T],Consec),
                                                       T \= [],
                                                       append(DepProcesadas,DepRestantes,DepTotales),
                                                       check_atribs([X|T],[],Consec,DepTotales,NoRaros),
                                                       %Volvemos a incorporar la dpendencia pero sin atribs raros
                                                       union_cjto(DepProcesadas,[dep(NoRaros,Consec)],NuevasProcesadas),
                                                       elim_atrib_raro(Cola,NuevasProcesadas,Nuevasdf), !.
                                                       
%en este caso la dependencia actual NO tiene antecedente compuesto
elim_atrib_raro(DepRestantes, DepProcesadas, Nuevasdf) :-
                                                       [Dep|Cola] = DepRestantes,
                                                       append(DepProcesadas,[Dep], Nuevasprocesadas),
                                                       elim_atrib_raro(Cola,Nuevasprocesadas,Nuevasdf), !.


%--------------------------elimina_atribs_raros(DepInit,DepFin)------------------------
%        Recibe el cjto de dependencias y devuelve el cjto de dependencias
%        habiendo eliminado los atributos raros de las dependencias
%--------------------------------------------------------------------------------------
elimina_atribs_raros(DepInit,DepFin) :-
                                     elim_atrib_raro(DepInit,[],DepFin).





%--------------------------recub_minimo(DepIni,DepFin)-------------------------------------
%        Calcula el recubrimiento mínimo (DepFin) de un cjto de Dependencias (DepIni)
%------------------------------------------------------------------------------------------
                                     
recub_minimo(DepIni,DepFin) :-
                            prepara_df(DepIni,Dep2),
                            elimina_redundantes(Dep2,Dep3),
                            elimina_atribs_raros(Dep3,DepFin).
                            
                            
                            
                            
%--------------------------is_in_deps(Atrib,Deps)-------------------------------------
%      Nos dice si el atributo Atrib aparece en alguna dependencia de la lista Deps,
%      ya sea como antecedente o consecuente
%-------------------------------------------------------------------------------------

is_in_deps(_,[]) :- !, fail.

is_in_deps(X,[Dep|_]) :-
                      Dep = dep(Ant,Consec),
                      (pertenece(X,Ant) | pertenece(X,Consec)), !.
                               
is_in_deps(X,[_|DepTail]) :-
                               is_in_deps(X,DepTail).



%--------------------------atribs_indep(Atribs,Deps,Indep)----------------------------
%      Nos dice qué atributos del cjto Atribs son independientes, es decir
%      que no aparecen en ninguna dependencia del cjto Deps
%-------------------------------------------------------------------------------------
atribs_indep([],_,[]).
atribs_indep([H|T],Deps,Indep) :-
                                  is_in_deps(H,Deps),
                                  atribs_indep(T,Deps,Indep), !.

%Recibe el contexto, las dependencias y devuelve una lista de los atributos independientes
atribs_indep([H|T],Deps,Indep) :-
                                  atribs_indep(T,Deps,IndepAux),
                                  append([H],IndepAux,Indep).
                                  
                                  
                                  
                                  
%--------------------------is_not_consec(Atrib,Deps)----------------------------
%      Nos dice si el atributo Atrib NO aparece en ningun consecuente de las
%      dependencias Deps.
%-------------------------------------------------------------------------------

is_not_consec(_,[]) :- !.

is_not_consec(X,[H|_]) :-
                         H = dep(_,Consec),
                         pertenece(X,Consec), !, fail.
                         
is_not_consec(X,[_|T]) :-
                         is_not_consec(X,T).



%--------------------------antecedentes_only(Atribs,Deps,Sol)-------------------
%      Nos devuelve en Sol el subcjto de los atributos Atribs, que
%      sólo aparecen como antecentes en las dependencias Deps
%-------------------------------------------------------------------------------
antecedentes_only([],_,[]) :- !.
antecedentes_only([H|T],Deps,Anteced) :-
                                         is_not_consec(H,Deps),
                                         antecedentes_only(T,Deps,Antaux),
                                         append([H],Antaux,Anteced),!.
antecedentes_only([_|T],Deps,Anteced) :-
                                         antecedentes_only(T,Deps,Anteced).
                                         
                                         
%--------------------------is_not_anteced(Atrib,Deps)----------------------------
%      Nos dice si el atributo Atrib NO aparece en ningun antecedente de las
%      dependencias Deps.
%-------------------------------------------------------------------------------

is_not_anteced(_,[]) :- !.

is_not_anteced(X,[H|_]) :-
                         H = dep(Anteced,_),
                         pertenece(X,Anteced), !, fail.
is_not_anteced(X,[_|T]) :-
                         is_not_anteced(X,T).


%--------------------------consecuentes_only(Atribs,Deps,Sol)-------------------
%      Nos devuelve en Sol el subcjto de los atributos Atribs, que
%      sólo aparecen como consecuentes en las dependencias Deps
%-------------------------------------------------------------------------------
consecuentes_only([],_,[]) :- !.
consecuentes_only([H|T],Deps,Consec) :-
                                         is_not_anteced(H,Deps),
                                         consecuentes_only(T,Deps,Consaux),
                                         append([H],Consaux,Consec),!.
consecuentes_only([_|T],Deps,Consec) :-
                                         consecuentes_only(T,Deps,Consec).


%--------------------------is_contained(Key,KeyList)----------------------------
%   Nos dice si la clave Key es un subcjto de las claves de la lista KeyList
%-------------------------------------------------------------------------------

is_contained(_,[]) :- !, fail.
is_contained(CurKey,[Key|_]) :-
                       dif_cjto(Key,CurKey,Diff),
                       Diff == [], !.
is_contained(CurKey,[_|T]) :-
                       is_contained(CurKey,T).
                       
                       
                       
%------combinaciones_claves(Seguro,Posibles,Todos,Deps,Ac,Claves)---------------
%   Este predicado calcula las combinaciones de claves, es usado por el
%   predicado posibles claves
%-------------------------------------------------------------------------------

combinaciones_claves(_,[],_,_,Ac,Ac) :- !.
combinaciones_claves(Seguro,[H|T],Todos,Deps,Ac,Claves) :-
                                                     append(Seguro,[H],Newcjto),
                                                     \+is_contained(Newcjto,Ac),
                                                     cierre(Newcjto,Deps,Cierre),
                                                     subcjto(Todos,Cierre),
                                                     append(Ac,[Newcjto],Acaux),
                                                     combinaciones_claves(Seguro,T,Todos,Deps,Acaux,Claves), !.
combinaciones_claves(Seguro,[H|T],Todos,Deps,Ac,Claves) :-
                                                     append(Seguro,[H],Newcjto),
                                                     is_contained(Newcjto,Ac),
                                                     combinaciones_claves(Seguro,T,Todos,Deps,Ac,Claves), !.
combinaciones_claves(Seguro,[H|T],Todos,Deps,Ac,Claves) :-
                                                     append(Seguro,[H],Newcjto),
                                                     combinaciones_claves(Seguro,T,Todos,Deps,Ac,Clavesaux),
                                                     combinaciones_claves(Newcjto,T,Todos,Deps,Clavesaux,Claves).
                                                     
                                                     
%------posibles_claves(Seguro,Posibles,Todos,Deps,Claves)-----------------------
%   Este predicado calcula las posibles claves:
%
%   Hay un cjto Seguro:
%       Son atributos que DEBEN pertenecer a todas las claves,
%       son los que son sólo antecedentes
%
%   Hay un cjto Posibles:
%       Son atributos que PUEDEN o no pertenecer a las claves,
%       son los que son antecedentes y consecuentes
%
%   Hay un cjto Todos:
%       Son "todos" los atributos, si un cjto de atributos contiene en su cierre
%       a estos atributos, lo consideraremos superclave
%-------------------------------------------------------------------------------
posibles_claves([],Posibles,Todos,Deps,Claves) :-
                                               combinaciones_claves([],Posibles,Todos,Deps,[],Claves), !.

posibles_claves(Seguro,_,Todos,Deps, Claves) :-
                                                   cierre(Seguro,Deps,CierreSeg),
                                                   subcjto(CierreSeg,Todos),
                                                   subcjto(Todos,CierreSeg),
                                                   append([],[Seguro],Claves), !.
%Con solo los antecedentes no es suficiente
posibles_claves(Seguro,Posibles,Todos,Deps,Claves) :-
                                                   combinaciones_claves(Seguro,Posibles,Todos,Deps,[],Claves).





%-----------------add_to_keys(Atribs,Claves,NuevasClaves)-----------------------
%   Este predicado añade un cjto de atributos Atribs a todas las claves dadas.
%
%   Se usa para el calculo de claves, ya que en el cálculo de claves inicialmente
%   ignoramos los atributos independientes, cálculamos las claves como si no
%   existieran, y luego que se han obtenido las claves, se añaden los atribs indep
%   a todas las claves calculadas anteriormente
%-------------------------------------------------------------------------------

add_to_keys([],OldKeys,OldKeys) :- !.

add_to_keys(_,[],[]):- !.

add_to_keys(Indep,[Key|Tail],NewKeys) :-
                                      add_to_keys(Indep,Tail,NewKaux),
                                      append(Key,Indep,NKey),
                                      append([NKey],NewKaux,NewKeys).






%-----------------claves_rel(Contexto,Dependencias,Claves)--------------------------------------------------
%   Este predicado calcula las claves de una relación.
%
%   Se calculan de la siguiente manera.
%      1) Se haya el recub minimo y apartir de aqui se trabaja solo con el recub min
%      2)Se calculan qué atributos son independientes
%      3)Obtenemos un nuevo cjto de atributos llamados NO independientes
%      4)Calculamos los Atributos que sólo aparecen como antecedente ya que DEBEN pertenecer a las clave
%      5)Calculamos los Atributos que son antecedentes y consecuentes, ya que PUEDEN pertenecer a las clave
%      6)Calculamos posibles claves sin tener en cuenta atribs independientes
%      7)A todas las claves calculadas le añadimos lso atribs indeps

%-------------------------------------------------------------------------------
claves_rel(Cont, [], [Cont]) :- !.
claves_rel(Cont, Deps, Claves) :-
                recub_minimo(Deps, Recub_min),
                atribs_indep(Cont,Recub_min,Indep),
                dif_cjto(Cont,Indep,AtribsNoIndep),
                antecedentes_only(AtribsNoIndep,Recub_min,Anteced),
                consecuentes_only(AtribsNoIndep,Recub_min,Consec),
                dif_cjto(AtribsNoIndep,Consec,NoConsec),
                dif_cjto(NoConsec,Anteced,AnteConsec),
                posibles_claves(Anteced,AnteConsec,AtribsNoIndep,Recub_min,Clavesaux),
                add_to_keys(Indep,Clavesaux,Claves).




%--------------------------get_Atribs_Prim(Claves,Atribs)------------------------------
%   Simplemente es una linealización de la lista de listas Claves en una lista lineal
%---------------------------------------------------------------------------------------

get_Atribs_Prim([],[]).

get_Atribs_Prim([Clave|T],Primos) :-
                                  get_Atribs_Prim(T,PrimAux),
                                  union_cjto(Clave,PrimAux,Primos).

%--------------------------save_in_bc(Dependencias)------------------------------------
%   Recorre las dependencias y las guarda en la base de conocimientos (PARA USO FUTURO).
%---------------------------------------------------------------------------------------

save_in_bc([]).

save_in_bc([dep(Antec,Consec)|T]) :-
                    assert(dependencia(Antec,Consec)),
                    save_in_bc(T).


%-------------------------clean_anom_2fn_aux(Anom,ListAnom,Result)-----------------------
%   Predicado auxiliar que se encarga de buscar las anomalias de 2fn analogas (referidas al mismo no primo)
%   y conserva la más específica entre ellas.
%----------------------------------------------------------------------------------------



clean_anom_2fn_aux(Anom,[],[Anom]) :- !.
clean_anom_2fn_aux(anomalia_2fn(Clave,Diff,NoPrimo),[anomalia_2fn(_,Diff2,NoPrimo)|T],CleanAnoms) :-
                                                                                                        subcjto(Diff,Diff2),
                                                                                                        clean_anom_2fn_aux(anomalia_2fn(Clave,Diff,NoPrimo),T, CleanAnoms), !.
                                                                                                        
clean_anom_2fn_aux(anomalia_2fn(_,Diff,NoPrimo),[anomalia_2fn(Clave2,Diff2,NoPrimo)|T],CleanAnoms) :-
                                                                                                        subcjto(Diff2,Diff),
                                                                                                        clean_anom_2fn_aux(anomalia_2fn(Clave2,Diff2,NoPrimo),T, CleanAnoms), !.
                                                                                                        
clean_anom_2fn_aux(anomalia_2fn(Clave,Diff,NoPrimo),[anomalia_2fn(Clave2,Diff2,NoPrimo2)|T],CleanAnoms) :-
                                                                                                        clean_anom_2fn_aux(anomalia_2fn(Clave,Diff,NoPrimo),T, CleanAux),
                                                                                                        append(CleanAux,[anomalia_2fn(Clave2,Diff2,NoPrimo2)],CleanAnoms).


%-------------------------clean_anomalias_2fn(ListaAnomalias,ListaAnomaliasLimpia)-----------------------
%   Predicado que se encarga de buscar las anomalias de 2fn analogas (referidas al mismo no primo)
%   y conserva la más específica entre ellas, devolviendo una lista lo mas simplificada y detallada posible
%   en cuanto a dependencias parciales.
%   Ej:
%      Si A,B,C son clave y D no depende totalmente de dicha clave (ya que A->D)
%      Es posible que en la lista encontremos anomalias que muestren:
%          - D depende parcialmente de A,B,C ya que depende de A,B
%          - D depende parcialmente de A,B,C ya que depende de A,C
%          - D depende parcialmente de A,B,C ya que depende de A
%      Siendo esta última la opción más valida, por eso este predicado eliminaría las primeras ds anomalias
%
%----------------------------------------------------------------------------------------
clean_anomalias_2fn([],[]):- !.

clean_anomalias_2fn([Anom|T],CleanAnoms) :-
                                      clean_anom_2fn_aux(Anom,T,CleanAux),
                                      (   ([Anom|T] \= CleanAux,
                                           clean_anomalias_2fn(CleanAux,CleanAnoms), !);
                                          (clean_anomalias_2fn(T,CleanAux2),
                                           append([Anom],CleanAux2,CleanAnoms),!)).

%--------------------------es_Parcial_detail(Clave,[],NoPrimos,Deps,Anomalias)-------------------
%   Comprueba si algun elemento de NoPrimos depende parcialmente de la clave
%   Se usa para comprobar la 2FN . Además muestra las anomalías que impiden la 2fn.
%------------------------------------------------------------------------------------------------
%Nota: en este predicado surge la estructura anomalia_2fn(arg1,arg2,arg3)
%                                                              arg1: Clave de la que algun Noprimo depende parcialmente
%                                                              arg2: Parte de la clave de la que depende algun NoPrimo
%                                                              arg3: No primo que depende de arg2 y por lotanto depende parcialmente de arg1


es_Parcial_detail([],_,_,_,_,[]) :- !.

es_Parcial_detail([Actual],Listos,_,_,Todos,[]) :-
                                            append(Listos,[Actual],Ataux),
                                            Ataux = Todos, !.
                                            

es_Parcial_detail([Actual|T],Listos,NoPrimo,Deps,Todos,Anomalias) :-
                                                             append(Listos,[Actual],Ataux),
                                                             cierre(Ataux,Deps,Cierre),
                                                             
                                                             ((pertenece(NoPrimo,Cierre),
                                                                   es_Parcial_detail(T,Listos,NoPrimo,Deps,Todos,AnomAux),
                                                                   Anomalias = [anomalia_2fn(Todos,Ataux,NoPrimo)|AnomAux], !);
                                                                   
                                                             (es_Parcial_detail(T,Listos,NoPrimo,Deps,Todos,AnomAux),               %no pertenece
                                                                   append(Listos,[Actual],ListoAux),
                                                                   es_Parcial_detail(T,ListoAux,NoPrimo,Deps,Todos,AnomAux2),
                                                                   append(AnomAux,AnomAux2,Anomalias),!)).
                                                                   
check_Parcial(_,[],_,[]) :- !.


check_Parcial(Clave,[H|T],Deps,Anomalias) :-
                                          es_Parcial_detail(Clave,[],H,Deps,Clave,Anoms),
                                          check_Parcial(Clave,T,Deps,Anoms2),
                                          append(Anoms,Anoms2,Anomalias), !.
                                              

%--------------------------is_2fn(Claves,NoPrimos,Deps)-----------------------------------
%   Comprueba si algun elemento de NoPrimos depende parcialmente de alguna de las claves, mostrando las que sean parciales
%-----------------------------------------------------------------------------------------
is_2fn_detail([],_,_,[]) :- !.

is_2fn_detail([Clave|T],NoPrimos,Deps,Anomalias) :-
                                check_Parcial(Clave,NoPrimos,Deps,Anoms),
                                clean_anomalias_2fn(Anoms,CleanAnom),
                                is_2fn_detail(T,NoPrimos,Deps,Errores),
                                append(CleanAnom,Errores,Anomalias).
                                
                       

%------------test_2fn(Contexto,Deps)----------------------------
%     Comprueba si la relación está en 2fn, en caso de no estarlo muestra los problemas que tiene
%---------------------------------------------------------------
test_2fn_detail(Cont,Deps,Errores) :-
                recub_minimo(Deps, Recub_min),
                claves_rel(Cont,Recub_min,Claves),
                get_Atribs_Prim(Claves,Primos),
                dif_cjto(Cont,Primos,NoPrimos),
                is_2fn_detail(Claves,NoPrimos,Recub_min,Errores).
                


%-------------------------- NOTA IMPORTANTE: -----------------------------------
%     los siguientes predicados son análogos a los anteriores pero son mas
%     eficientes al no calcular los problemas encontrados como dependencias
%     parciales
%-------------------------------------------------------------------------------


%--------------------------es_Parcial(Clave,[],NoPrimos,Deps)-------------------
%   Comprueba si algun elemento de NoPrimos depende parcialmente de la clave
%   Se usa para comprobar la 2FN
%-------------------------------------------------------------------------------

es_Parcial([],_,_,_) :- !, fail.

es_Parcial([_|T],Listos,NoPrimos,Deps) :-
                                              append(Listos,T,Ataux),
                                              cierre(Ataux,Deps,Cierre),
                                              dif_cjto(Cierre,NoPrimos,Diff),
                                              Diff \= Cierre, !.

es_Parcial([Atrib|T],Listos,NoPrimos,Deps) :-
                                              es_Parcial(T,[Atrib|Listos],NoPrimos,Deps).




%--------------------------is_2fn(Claves,NoPrimos,Deps)-----------------------------------
%   Comprueba si algun elemento de NoPrimos depende parcialmente de alguna de las claves
%-----------------------------------------------------------------------------------------
is_2fn([],_,_) :- !.

is_2fn([Clave|T],NoPrimos,Deps) :-
                                \+es_Parcial(Clave,[],NoPrimos,Deps),
                                is_2fn(T,NoPrimos,Deps).


%------------test_2fn(Contexto,Deps)----------------------------
%     Comprueba si la relación está en 2fn
%---------------------------------------------------------------
test_2fn(Cont,Deps) :-
                recub_minimo(Deps, Recub_min),
                claves_rel(Cont,Recub_min,Claves),
                get_Atribs_Prim(Claves,Primos),
                dif_cjto(Cont,Primos,NoPrimos),
                is_2fn(Claves,NoPrimos,Recub_min).




%--------------------------is_3fn(Contexto,Primos,Deps,Deps)-----------------------------------
%   Comprueba si todas las dependencias cumplen que el antecedente es superclave o
%   el consecuente es un atributo primo
%-----------------------------------------------------------------------------------------
                
is_3fn(_,_,_,[]) :- !.

%Comprobamos si antecedente es superclave
is_3fn(Cont,Primos,TotalDeps,[dep(Antec,_)|T]) :-
                                                    cierre(Antec,TotalDeps,Cierre),
                                                    subcjto(Cont,Cierre),
                                                    subcjto(Cierre,Cont), ! ,
                                                    is_3fn(Cont,Primos,TotalDeps,T).
%Comprobamos si consecuente es primo
is_3fn(Cont,Primos,TotalDeps,[dep(_,[Consec])|T]) :-
                                                    pertenece(Consec,Primos),
                                                    is_3fn(Cont,Primos,TotalDeps,T).



%------------test_3fn(Contexto,Deps)----------------------------
%     Comprueba si la relación está en 3fn
%---------------------------------------------------------------
test_3fn(Cont,Deps) :-
                recub_minimo(Deps, Recub_min),
                claves_rel(Cont,Recub_min,Claves),
                get_Atribs_Prim(Claves,Primos),
                dif_cjto(Cont,Primos,NoPrimos),
                is_2fn(Claves,NoPrimos,Recub_min),
                is_3fn(Cont,Primos,Recub_min,Recub_min).
                
                
                

%--------------------------is_3fn(Contexto,Primos,Deps,Deps)-----------------------------------
%   Comprueba si todas las dependencias cumplen que el antecedente es superclave o
%   el consecuente es un atributo primo
%-----------------------------------------------------------------------------------------

is_3fn_detail(_,_,_,[],[]) :- !.

%Comprobamos si antecedente es superclave
is_3fn_detail(Cont,Primos,TotalDeps,[dep(Antec,[Consec])|T],Anomalias) :-
                                                                ((cierre(Antec,TotalDeps,Cierre),
                                                                  subcjto(Cont,Cierre),
                                                                  subcjto(Cierre,Cont), !);
                                                                 (pertenece(Consec,Primos), !)), ! ,
                                                                is_3fn_detail(Cont,Primos,TotalDeps,T,Anomalias).
%Comprobamos si consecuente es primo
is_3fn_detail(Cont,Primos,TotalDeps,[Dep|T],Anomalias) :-
                                                     is_3fn_detail(Cont,Primos,TotalDeps,T,AnomAux),
                                                     Anomalias = [Dep|AnomAux].



%------------test_3fn(Contexto,Deps)----------------------------
%     Comprueba si la relación está en 3fn
%---------------------------------------------------------------
test_3fn_detail(Cont,Deps,Anomalias2FN,Anomalias3FN) :-
                recub_minimo(Deps, Recub_min),
                claves_rel(Cont,Recub_min,Claves),
                get_Atribs_Prim(Claves,Primos),
                dif_cjto(Cont,Primos,NoPrimos),
                is_2fn_detail(Claves,NoPrimos,Recub_min,Anomalias2FN),
                ((Anomalias2FN \= [], Anomalias3FN = [],!);
                (is_3fn_detail(Cont,Primos,Recub_min,Recub_min,Anomalias3FN),!)).





%--------------------------is_fnbc(Contexto,Deps,Deps)-----------------------------------
%   Comprueba si todas las dependencias cumplen que el antecedente es superclave
%-----------------------------------------------------------------------------------------
                
is_fnbc(_,_,[]) :- !.

is_fnbc(Cont,Deps,[dep(Antec,_)|T]) :-
                                    cierre(Antec,Deps,Cierre),
                                    subcjto(Cierre,Cont),
                                    subcjto(Cont,Cierre), !,
                                    is_fnbc(Cont,Deps,T).

                                         
%------------test_fnbc(Contexto,Deps)----------------------------
%     Comprueba si la relación está en fnbc
%---------------------------------------------------------------
test_fnbc(Cont,Deps) :-
                recub_minimo(Deps, Recmin),
                claves_rel(Cont,Recmin,Claves),
                get_Atribs_Prim(Claves,Primos),
                dif_cjto(Cont,Primos,NoPrimos),
                is_2fn(Claves,NoPrimos,Recmin),
                is_3fn(Cont,Primos,Recmin,Recmin),
                is_fnbc(Cont,Recmin, Recmin).


%--------------------------is_fnbc(Contexto,Deps,Deps)-----------------------------------
%   Comprueba si todas las dependencias cumplen que el antecedente es superclave
%-----------------------------------------------------------------------------------------

is_fnbc_detail(_,_,[],[]) :- !.

is_fnbc_detail(Cont,Deps,[dep(Antec,_)|T],AnomaliasFNBC) :-
                                                         cierre(Antec,Deps,Cierre),
                                                         subcjto(Cierre,Cont),
                                                         subcjto(Cont,Cierre), !,
                                                         is_fnbc_detail(Cont,Deps,T,AnomaliasFNBC),!.

is_fnbc_detail(Cont,Deps,[Dep|T],AnomaliasFNBC) :-
                                                is_fnbc_detail(Cont,Deps,T,AnomsAux),
                                                AnomaliasFNBC = [Dep|AnomsAux], !.
                                                



%------------test_fnbc(Contexto,Deps)----------------------------
%     Comprueba si la relación está en fnbc
%---------------------------------------------------------------
test_fnbc_detail(Cont,Deps,Anomalias2FN,Anomalias3FN,AnomaliasFNBC) :-
                                                                    recub_minimo(Deps, Recmin),
                                                                    claves_rel(Cont,Recmin,Claves),
                                                                    get_Atribs_Prim(Claves,Primos),
                                                                    dif_cjto(Cont,Primos,NoPrimos),
                                                                    is_2fn_detail(Claves,NoPrimos,Recmin,Anomalias2FN),
                                                                    ( (Anomalias2FN \= [], Anomalias3FN=[],AnomaliasFNBC=[],!);
                                                                      (is_3fn_detail(Cont,Primos,Recmin,Recmin,Anomalias3FN),
                                                                      ( (Anomalias3FN \=[], AnomaliasFNBC = [], !);
                                                                        (is_fnbc_detail(Cont,Recmin, Recmin,AnomaliasFNBC))
                                                                       )
                                                                       )
                                                                    ).



%------------Predicados para el algoritmo de síntesis----------------------------
%
%--------------------------------------------------------------------------------

%Tamaño de una lista
list_size([],0).

list_size([_|T],Cont) :-
                      list_size(T,Caux),
                      Cont is Caux + 1.
                      
                      
                      
                      
%Obtiene una lista de todos los atributos de las dependencias
get_atribs([],[]) :- !.

get_atribs([dep(Antec,Consec)|Cola],Atribs) :-
                                            get_atribs(Cola,AtribsAc),
                                            union_cjto(AtribsAc,Consec,Aux),
                                            union_cjto(Antec,Aux,Atribs).




%Busca las dependencias cuyo antecedente sea el indicado en el 1 argumento.

busca_antec(_,[],[],[]) :- !.
busca_antec(Atribs,[dep(Ant,Cons)|ColaDeps],Sol,Rest) :-
                                                      dif_cjto(Atribs,Ant,[]),
                                                      dif_cjto(Ant,Atribs,[]),
                                                      busca_antec(Atribs,ColaDeps,SolAc,Rest),
                                                      Sol = [dep(Ant,Cons)|SolAc], !.
busca_antec(Atribs,[Dep|ColaDeps],Sol,Rest) :-
                                            busca_antec(Atribs,ColaDeps,Sol,RestAc),
                                            Rest = [Dep|RestAc].
                                            
                                            
                                            
                                            
                                            

%Dado unc cjto de dependencias, obtenemos subcjtos de dependencias, agrupados por el antecedente
sint_aux([],[]) :- !.
sint_aux([dep(Antec,Consec)|ColaDeps],DepsRel) :-
                                                  busca_antec(Antec,[dep(Antec,Consec)|ColaDeps],DepsG1,Rest),
                                                  sint_aux(Rest,DepsGRest),
                                                  DepsRel = [grupo([Antec],DepsG1)|DepsGRest].
                                                  
                                                  
                                                  
                                                  
                                                  

%Nos dice si debemos o no fusionar 2 grupos de dependencias, viendo si los antecedentes de cada grupo son equivalentes.

%Si antecedentes de dos grupos son equivalentes se debe fusionar
debe_fusionar(G1,G2,Deps) :-
                          cierre(G1,Deps,CG1),
                          dif_cjto(G2,CG1,[]),
                          cierre(G2,Deps,CG2),
                          dif_cjto(G1,CG2,[]), !.
                          
debe_fusionar(_,_,_) :- !, fail.






%Elimina de las dependencias con antecedente igual al 2 argumento y consecuente, cualquier subcjto del 3 argumento.

del_deps_with([],_,_,[]) :- !.
del_deps_with([dep(A,[C])|ColaDeps],Ant,Cons,NewDeps):-
                                                      dif_cjto(Ant,A,[]),
                                                      dif_cjto(A,Ant,[]),
                                                      pertenece(C,Cons),
                                                      del_deps_with(ColaDeps,Ant,Cons,NewDeps), !.
del_deps_with([Dep|ColaDeps],Ant,Cons,NewDeps):-
                                                del_deps_with(ColaDeps,Ant,Cons,NDepsAux),
                                                NewDeps = [Dep|NDepsAux].
                                                      
                                                      
                                                      
                                                      
                                                      
%Intenta fusionar un grupo con el resto de ellos comprobando si los antecedentes de ambos son equivalentes

fusiona_aux(G1,[],_,[],[],G1) :- !.
fusiona_aux(grupo([A1|T],DepsG1),[grupo([A2|T2],DepsG2)|GCola],Deps,J,Rest,Fusion) :-
                                                                             debe_fusionar(A1,A2,Deps),
                                                                             %BORRAMOS De G1 deps A1 -> X, X c A2 DEPS
                                                                             del_deps_with(DepsG1,A1,A2,NDepsG1),
                                                                             %BORRAMOS De G2 deps A2 -> X, X c A2 DEPS
                                                                             del_deps_with(DepsG2,A2,A1,NDepsG2),
                                                                             append(NDepsG1,NDepsG2,NDepsG),
                                                                             append([A1|T],[A2|T2],NewAnts),
                                                                             fusiona_aux(grupo(NewAnts,NDepsG),GCola,Deps,J2,Rest,Fusion),
                                                                             J = [dep(A1,A2)|[dep(A2,A1)|J2]], ! .
fusiona_aux(G1,[G2|GCola],Deps,J,Rest,Fusion) :-
                                              fusiona_aux(G1,GCola,Deps,J,RestAc,Fusion),
                                              Rest = [G2|RestAc].



%Añade unas dependencias a un grupo de dependencias
add_deps_group(Deps,grupo(Ants,GDeps),NewGru) :-
                                              append(Deps,GDeps,DepAux),
                                              recub_minimo(DepAux,Recmin),
                                              NewGru = grupo(Ants,Recmin).
                                              


%Intenta hacer fusiones entre los grupos de dependencias basándose en la equivalencia de atributos
fusiona_equiv([],_,[]):- !.

fusiona_equiv([G1|GCola],Deps,NewGrupos) :-
                                         fusiona_aux(G1,GCola,Deps,J,Rest,Fusion),
                                         add_deps_group(J,Fusion,NewFus),
                                         fusiona_equiv(Rest,Deps,NGruposAux),
                                         NewGrupos = [NewFus|NGruposAux].
                                                  




%No hemos encontrado ninguna clave en ninguna relacion asi que añadimos una clave cualquiera.

check_claves([],[Clave|_],[],[rel(Clave,[])]) :- !.
check_claves([],TotClave,[Rel|ColaRel],NewRel) :-
                                     check_claves(TotClave,TotClave,ColaRel,NewRelAc),
                                     NewRel = [Rel|NewRelAc], !.
check_claves([Clave|_],_,[rel(Contex,Deps)|ColaRel],[rel(Contex,Deps)|ColaRel]):-
                                                                                 dif_cjto(Clave,Contex,[]), !.
check_claves([_|ColaClave],TotClaves,Rels,NewRels):-
                                                    check_claves(ColaClave,TotClaves,Rels,NewRels).
                                                    
                                                    
                                                    
                                                    
                                                    
%Transforma la lista de grupos de dependencias en una lista de relaciones.
grupo_to_rel([],[]) :- !.
grupo_to_rel([grupo(_,Deps)|ColaG],Rels) :-
                                         grupo_to_rel(ColaG,RelsAux),
                                         get_atribs(Deps,Atribs),
                                         Rels = [rel(Atribs,Deps)|RelsAux].



%------------Algoritmo de síntesis-------------
%
%----------------------------------------------
sintesis(Contexto,Deps,NewRels):-
                                 recub_minimo(Deps,Recmin),
                                 sint_aux(Recmin,Grupos),
                                 fusiona_equiv(Grupos,Recmin,NewGrupos),
                                 grupo_to_rel(NewGrupos,Rels),
                                 
                                 claves_rel(Contexto,Recmin,Claves),
                                 %Comprueba que alguna clave esté en alguna relacion, si no esta añade una cualquiera a una nueva relacion
                                 check_claves(Claves,Claves,Rels,NewRels).


                                                              





