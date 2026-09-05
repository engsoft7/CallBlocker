@echo off
echo ========================================================
echo Fazendo o envio do seu aplicativo para o GitHub...
echo ========================================================
cd "C:\Users\HP\3D Objects\CallBlockerApp"
git remote set-url origin https://github.com/engsoft7/CallBlocker.git
git push -u origin main --force
echo ========================================================
echo Envio concluído! Pressione qualquer tecla para sair.
echo ========================================================
pause
