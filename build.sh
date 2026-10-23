#!/bin/sh
set -eu
: "${J2ME_HOME:?Defina J2ME_HOME para o diretório do Java ME SDK/WTK}"
echo "SDK configurado em: $J2ME_HOME"
echo "Este projeto requer javac com midp.jar/cldc.jar e preverify do SDK."
echo "Consulte README.md para a sequência de compilação."
