<template>
  <div id="view-container">
    <div id="welcome">
      🎉欢迎进入方言地图后台数据维护中心
    </div>
    <div id="backend-button-container">
      <button><router-link to="/languages">🌏大方言区数据🌏</router-link></button>
      <button><router-link to="/dialects">💡方言片数据💡</router-link></button>
      <button><router-link to="/subdialects">🙌方言小片数据🙌‍</router-link></button>
      <button><router-link to="/accents">🎈口音数据🎈</router-link></button>
    </div>

  </div>

</template>

<script>
export default {
  name: "Backend"
}
</script>

<style scoped>
  #view-container{
    width: 100vw;
    height: 100vh;
    background-color: rgba(225,250,225,80%);

  }
  #welcome{
    text-align: center;
    margin-top: 10%;
    /*background-color: #4a90e2;*/
    font-size: 32px;
    font-weight: bold;
    opacity: 0;
    transform: translateY(20px);
    animation: fadeUp 0.6s ease forwards;
  }
  #backend-button-container{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    height: 300px;
  }
  button
  {
    display: block;
    width: 200px;
    margin-top: 10px;
    opacity: 0;
    transform: translateY(20px);
    animation: fadeUp 0.7s ease forwards;
    background-color: rgb(76, 175, 80);
  }
  button:hover{
    scale: 105%;
  }
  @keyframes fadeUp {
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  :deep(a){
    color: white;
    text-decoration: none;
  }
</style>