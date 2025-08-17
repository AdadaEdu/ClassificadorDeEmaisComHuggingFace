#!/bin/bash

echo "🚀 Configurando servidor HuggingFace para classificação de e-mails..."
echo "================================================================"

# Verificar se Python 3 está instalado
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 não encontrado. Instalando..."
    sudo apt update
    sudo apt install -y python3 python3-pip python3-venv
fi

# Verificar se pip está instalado
if ! command -v pip3 &> /dev/null; then
    echo "❌ pip3 não encontrado. Instalando..."
    sudo apt install -y python3-pip
fi

# Criar ambiente virtual
echo "🔧 Criando ambiente virtual Python..."
python3 -m venv huggingface_env

# Ativar ambiente virtual
echo "🔧 Ativando ambiente virtual..."
source huggingface_env/bin/activate

# Atualizar pip
echo "📦 Atualizando pip..."
pip install --upgrade pip

# Instalar dependências
echo "📦 Instalando dependências Python..."
pip install -r requirements.txt

echo ""
echo "✅ Instalação concluída!"
echo ""
echo "🚀 Para executar o servidor HuggingFace:"
echo "   source huggingface_env/bin/activate"
echo "   python huggingface_server.py"
echo ""
echo "📡 O servidor estará disponível em: http://localhost:5000"
echo "🔗 A aplicação Java se conectará automaticamente"
echo ""
echo "💡 Dica: Execute em um terminal separado para manter o servidor rodando"
