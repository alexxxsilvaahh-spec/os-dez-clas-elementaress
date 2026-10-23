# Compilar Os Dez Clãs Elementares sem PC

Este projeto inclui `.github/workflows/build-j2me.yml`, que tenta compilar o MIDlet num runner Linux do GitHub Actions usando o compilador alternativo J2ME SDK (que declara incluir compilação, preverificação CLDC e empacotamento JAR/JAD).

## Instruções pelo celular Android

1. Entre em https://github.com e crie uma conta, se ainda não tiver.
2. Crie um repositório novo. Pode ser privado.
3. Envie os arquivos e pastas deste projeto para o repositório, preservando `.github/workflows/build-j2me.yml`, `src/`, `MANIFEST.MF` e `Os_Dez_Clas_Elementares.jad.template`.
4. Abra a aba **Actions** do repositório. Se aparecer a confirmação para habilitar workflows, confirme.
5. Selecione **Compilar Os Dez Clãs Elementares (J2ME)** e toque em **Run workflow**. Também pode iniciar automaticamente quando enviar os arquivos.
6. Abra a execução mais recente. Se ela ficar verde, procure a seção **Artifacts** e baixe `Os-Dez-Clas-Elementares-J2ME`.
7. Extraia o ZIP do artifact no Android e importe `Os_Dez_Clas_Elementares.jar` no J2ME Loader. O `.jad` é fornecido para referência e outros instaladores.

## Transparência

O workflow ainda precisa ser executado num runner real para confirmar se o compilador alternativo aceita este código. Um workflow verde confirma que os arquivos foram produzidos pela pipeline, não que o jogo foi testado dentro do J2ME Loader. Se falhar, abra os logs da etapa vermelha e compartilhe a mensagem de erro para que o projeto seja corrigido.
