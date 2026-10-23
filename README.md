# Os Dez Clãs Elementares — versão 1.0

Jogo de luta 2D para Java ME (MIDP 2.0 / CLDC 1.1), com resolução-alvo de 240×320.
O projeto é desenhado por código, sem assets externos. Inclui menu, dez adversários/clãs, barras de vida, pontuação, ataques, técnica especial, defesa, animação simples, vitória e derrota.

## Controles
- 4 / 6: andar para esquerda/direita (segure para caminhar)
- 2 / 8: mover-se no eixo vertical
- 5: golpe normal / confirmar menu
- 7 ou 9: técnica especial
- 0: defender
- 2 / 8 no menu: navegar

## Compilação real para JAR
É necessário um Java ME SDK/WTK com `midp.jar`, `cldc.jar` e a ferramenta `preverify`. Um JDK convencional não inclui as APIs Java ME.
1. Configure `J2ME_HOME` para a instalação do SDK.
2. Compile com o `javac` do JDK, adicionando os JARs MIDP/CLDC ao classpath.
3. Rode `preverify` na pasta de classes usando a biblioteca CLDC.
4. Crie o JAR contendo as classes pré-verificadas e o `MANIFEST.MF`.
5. Atualize `MIDlet-Jar-Size` no JAD para o tamanho exato do JAR.
6. Abra o JAR/JAD num emulador MIDP 2.0.

## Status honesto
Este pacote é o projeto-fonte da versão 1.0. Não há um JAR validado incluído porque este ambiente não tem bibliotecas Java ME nem `preverify`; não seria correto entregar um JAR vazio ou um arquivo Java SE como se fosse instalável. Teste final em emulador ainda é necessário.
