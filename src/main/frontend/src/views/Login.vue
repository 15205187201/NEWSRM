<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-logo">
        <h3>SRM系统</h3>
      </div>
      <div class="login-form">
        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="loginForm.username" prefix-icon="el-icon-user"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input 
              v-model="loginForm.password" 
              prefix-icon="el-icon-lock" 
              type="password"
            ></el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="captchaCode">
            <el-input 
              v-model="loginForm.captchaCode" 
              prefix-icon="el-icon-picture" 
              style="width: 60%; margin-right: 10px;"
            ></el-input>
            <img 
              :src="captchaUrl" 
              alt="验证码" 
              @click="refreshCaptcha" 
              class="captcha-img"
            >
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin">登录</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        captchaId: '',
        captchaCode: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        captchaCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      },
      captchaUrl: ''
    }
  },
  created() {
    this.refreshCaptcha();
  },
  methods: {
    refreshCaptcha() {
      this.axios.get('/api/auth/captcha')
        .then(res => {
          if (res.code === 200) {
            this.loginForm.captchaId = res.data.id;
            this.captchaUrl = 'data:image/png;base64,' + res.data.image;
          } else {
            this.$message.error(res.message || '获取验证码失败');
          }
        })
        .catch(err => {
          console.error(err);
          this.$message.error('获取验证码失败');
        });
    },
    handleLogin() {
      this.$refs.loginFormRef.validate(valid => {
        if (valid) {
          this.axios.post('/api/auth/login', this.loginForm)
            .then(res => {
              if (res.code === 200) {
                localStorage.setItem('token', res.data.token);
                this.$router.push({ name: 'Home' });
              } else {
                this.refreshCaptcha();
                this.$message.error(res.message || '登录失败');
              }
            })
            .catch(err => {
              console.error(err);
              this.refreshCaptcha();
              this.$message.error('登录失败');
            });
        } else {
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.loginFormRef.resetFields();
      this.refreshCaptcha();
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.login-logo {
  text-align: center;
  margin-bottom: 30px;
}

.captcha-img {
  height: 40px;
  cursor: pointer;
  vertical-align: middle;
}
</style>    